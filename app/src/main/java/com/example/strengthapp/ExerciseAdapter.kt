package com.example.strengthapp

import Models.Exercise
import Models.JsonResponse
import Services.ApiCaller
import Services.SharedPreferenceManager
import Utils.createSnackbar
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers;
import kotlinx.android.synthetic.main.recycler_view_exercise_list.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class ExerciseAdapter(val context: Context) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    val client by lazy { ApiCaller.create() }
    var exercises: ArrayList<Exercise> = ArrayList()
    var search = 0

    init { refreshExercises() }

    class ExerciseViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ExerciseViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_exercise_list, parent, false)

        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        println("Ovdje sam bio - bindViewHolder")
        holder.view.name_set.text = "%s".format(exercises[position].name)
        if(search == 1) {
            holder.view.subtext.text = "Click here to add exercise"
            if(exercises[position].id != null) {
                holder.view.ex_card.setOnClickListener { v ->
                    val result = addExercise(v, SharedPreferenceManager.getCurrentWorkout(context), exercises[position].id)
                    if(result) {
                        val intent = Intent(context, WorkoutActivity::class.java)
                        intent.putExtra("workoutID", SharedPreferenceManager.getCurrentWorkout(context))
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        ContextCompat.startActivity(context, intent, null)
                    }
                }
            }
        }
        else {
            holder.view.subtext.text = "Click here to see more information"
            if(exercises[position].id != null) {
                holder.view.ex_card.setOnClickListener { v ->
                    var intent = Intent(context, ExerciseViewActivity::class.java)
                    if(search == 2) { intent = Intent(context, RequestViewActivity::class.java) }
                    intent.putExtra("exerciseID", exercises[position].id)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    ContextCompat.startActivity(context, intent, null)
                }
            }
        }
    }

    override fun getItemCount() = exercises.size

    fun refreshExercises() {
        search = 0
        client.getExercises()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                exercises.clear()
                exercises.addAll(result.exercises)
                notifyDataSetChanged()
            },{ error ->
                Toast.makeText(context, "Refresh error: ${error.message}", Toast.LENGTH_LONG).show()
            })
    }

    fun refreshRequests() {
        search = 2
        client.getRequests()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                exercises.clear()
                exercises.addAll(result.exercises)
                exercises.reverse()
                notifyDataSetChanged()
            },{ error ->
                Toast.makeText(context, "Refresh error: ${error.message}", Toast.LENGTH_LONG).show()
            })
    }

    fun refreshSearch(input: String) {
        search = 1
        val json: JsonObject = JsonObject()
        json.addProperty("input", input)
        client.searchExercises(json)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({result ->
                exercises.clear()
                exercises.addAll(result.exercises)
                notifyDataSetChanged()
            },{ error ->
                Toast.makeText(context, "Refresh error: ${error.message}", Toast.LENGTH_LONG).show()
            })
    }

    fun addExercise(v:View, workout: Int, exercise: Int): Boolean {
        val json: JsonObject = JsonObject()
        val jsoninside: JsonObject = JsonObject()
        jsoninside.addProperty("workout_id", workout)
        jsoninside.addProperty("exercise_id", exercise)
        json.add("workoutExerciseId", jsoninside)

        val call = client.addWorkoutExercise(json)
        var k = 0

        call.enqueue(object: Callback<JsonResponse> {
            override fun onResponse(call: Call<JsonResponse>, response: Response<JsonResponse>) {
                if(response.code() == 200) {
                    val response_body = response.body()

                    if (response_body?.message?.contains("success")!!) {
                        val intent = Intent(context, WorkoutActivity::class.java)
                        intent.putExtra("workoutID", SharedPreferenceManager.getCurrentWorkout(context))
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        ContextCompat.startActivity(context, intent, null)
                    } else {
                        createSnackbar(v, response_body.message.toString(), Color.RED)
                    }
                }
            }
            override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                createSnackbar(v, t.message.toString(), Color.RED)
            }
        })
        if(k == 1) return true
        else return false
    }
}