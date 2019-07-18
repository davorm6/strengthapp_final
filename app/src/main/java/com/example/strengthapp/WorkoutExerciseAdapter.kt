package com.example.strengthapp

import Models.Exercise
import Models.ExerciseSet
import Models.JsonResponse
import Models.WorkoutExercise
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
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.recycler_view_item.view.*
import io.reactivex.android.schedulers.AndroidSchedulers;
import kotlinx.android.synthetic.main.recycle_view_calendar.view.*
import kotlinx.android.synthetic.main.recycler_view_exercise_list.view.*
import kotlinx.android.synthetic.main.recycler_view_sets_list.view.*
import kotlinx.android.synthetic.main.recycler_view_workout.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WorkoutExerciseAdapter(val context: Context) : RecyclerView.Adapter<WorkoutExerciseAdapter.WorkoutExerciseViewHolder>() {

    val client by lazy { ApiCaller.create() }
    var exercises: ArrayList<WorkoutExercise> = ArrayList()

    init { refreshExercises(SharedPreferenceManager.getCurrentWorkout(context)) }

    class WorkoutExerciseViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): WorkoutExerciseViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_workout, parent, false)

        return WorkoutExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutExerciseViewHolder, position: Int) {
        println("Ovdje sam bio - bindViewHolder")
        holder.view.exercise.text = exercises[position].exercise.name
        var volume = 0
        for(set in exercises[position].sets) {
            volume += set.weight * set.repetitions
        }
        holder.view.sets.text = "Number of sets: %d / volume: %d".format(exercises[position].sets.size, volume)

        holder.view.exer_card.setOnClickListener { v ->
            val intent = Intent(context, WorkoutExerciseView::class.java)
            intent.putExtra("exerciseID", exercises[position].workoutExerciseId.exercise_id)
            intent.putExtra("exerciseName", exercises[position].exercise.name)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            ContextCompat.startActivity(context, intent, null)
        }
    }

    override fun getItemCount() = exercises.size

    fun refreshExercises(id: Int) {
        client.getWorkoutExercises(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                exercises.clear()
                exercises.addAll(result.exercises)
                exercises.sortBy { it.ordinal_number }
                notifyDataSetChanged()
            },{ error ->
                Toast.makeText(context, "Refresh error: ${error.message}", Toast.LENGTH_LONG).show()
            })
    }
}