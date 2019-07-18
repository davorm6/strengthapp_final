package com.example.strengthapp

import Models.ExerciseSet
import Models.JsonResponse
import Models.Workout
import Models.WorkoutExerciseId
import Services.ApiCaller
import Services.SharedPreferenceManager
import Utils.createSnackbar
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers;
import kotlinx.android.synthetic.main.activity_workout_exercise_add.*
import kotlinx.android.synthetic.main.recycler_view_sets_list.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class ExerciseSetAdapter(val context: Context) : RecyclerView.Adapter<ExerciseSetAdapter.ExerciseSetViewHolder>() {

    val client by lazy { ApiCaller.create() }
    var sets: ArrayList<ExerciseSet> = ArrayList()

    init { refreshSets(SharedPreferenceManager.getCurrentWorkout(context), 0) }

    class ExerciseSetViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ExerciseSetViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_sets_list, parent, false)

        return ExerciseSetViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseSetViewHolder, position: Int) {
        var weight = sets[position].weight
        var reps = sets[position].repetitions

        val json: JsonObject = JsonObject()
        json.addProperty("workoutID", sets[position].exerciseSetId.workoutExerciseId.workout_id)
        json.addProperty("exerciseID", sets[position].exerciseSetId.workoutExerciseId.exercise_id)
        json.addProperty("setID", sets[position].exerciseSetId.set_number)

        if(sets.size > 0) {
            holder.view.ex_set_card.name_set.text = "#%d set".format(position+1)
            holder.view.ex_set_card.weight.setText("%d".format(sets[position].weight))
            holder.view.ex_set_card.reps.setText("%d".format(sets[position].repetitions))
        }

        holder.view.ex_set_card.weight.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                    if(holder.view.ex_set_card.weight.text != null && holder.view.ex_set_card.weight.text!!.isNotEmpty()) {
                        if (weight != holder.view.ex_set_card.weight.text.toString().toInt()) {
                            json.remove("repetitions")
                            json.addProperty("weight", holder.view.ex_set_card.weight.text.toString().toInt())
                            val call_w = client.editWorkoutExerciseSet(json)
                            if(holder.view.ex_set_card.weight.text.toString().toInt() > 0) {
                                call_w.enqueue(object : Callback<JsonResponse> {
                                    override fun onResponse(
                                        call: Call<JsonResponse>,
                                        response: Response<JsonResponse>
                                    ) {
                                    }

                                    override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                                    }
                                })
                                weight = holder.view.ex_set_card.weight.text.toString().toInt()
                            }
                            else {
                                createSnackbar(holder.view, "Weight can't be lower than 0.", Color.RED)
                            }
                        }
                    }
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }
            })

        holder.view.ex_set_card.reps.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if(holder.view.ex_set_card.reps.text != null && holder.view.ex_set_card.reps.text!!.isNotEmpty()) {
                    if (reps != holder.view.ex_set_card.reps.text.toString().toInt()) {
                        json.remove("weight")
                        json.addProperty("repetitions", holder.view.ex_set_card.reps.text.toString().toInt())
                        val call_r = client.editWorkoutExerciseSet(json)
                        if(holder.view.ex_set_card.reps.text.toString().toInt() > 0) {
                            call_r.enqueue(object : Callback<JsonResponse> {
                                override fun onResponse(call: Call<JsonResponse>, response: Response<JsonResponse>) {
                                }

                                override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                                }
                            })
                            reps = holder.view.ex_set_card.reps.text.toString().toInt()
                        }
                        else {
                            createSnackbar(holder.view, "Repetitions can't be lower than 0.", Color.RED)
                        }
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        holder.view.ex_set_card.delbutton.setOnClickListener { v ->
            val builder = AlertDialog.Builder(v.context)
            builder.setTitle("Remove set")
            builder.setMessage("Are you sure you want to delete this set?")

            builder.setPositiveButton("Yes") { _, _ ->
                val call2 = client.deleteWorkoutExerciseSet(json)
                call2.enqueue(object: Callback<JsonResponse> {
                    override fun onResponse(call: Call<JsonResponse>, response: Response<JsonResponse>) {
                        if(response.code() == 200) {
                            createSnackbar(v, response.body()?.message!!, Color.RED)
                            refreshSets(sets[position].exerciseSetId.workoutExerciseId.workout_id, sets[position].exerciseSetId.workoutExerciseId.exercise_id)
                        }
                    }
                    override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                        createSnackbar(v, t.message.toString(), Color.RED)
                    }
                })
            }
            builder.setNegativeButton("No") {_, _ ->

            }
            builder.show()
        }
    }

    override fun getItemCount() = sets.size

    fun maxSet(): Int {
        var max = 0
        for(set in sets) {
            if(set.exerciseSetId.set_number > max) {
                max = set.exerciseSetId.set_number
            }
        }
        return max
    }

    fun refreshSets(wid: Int, eid: Int) {
        client.getWorkoutExercises(wid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                sets.clear()
                for(workout in result.exercises) {
                    if(workout.workoutExerciseId.exercise_id == eid) {
                        sets.addAll(workout.sets)
                    }
                }
                sets.sortBy { it.exerciseSetId.set_number }
                notifyDataSetChanged()
            },{ error ->
                Toast.makeText(context, "Refresh error: ${error.message}", Toast.LENGTH_LONG).show()
            })
    }
}