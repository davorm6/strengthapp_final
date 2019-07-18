package com.example.strengthapp

import Models.JsonWorkoutResponse
import Models.Workout
import Models.WorkoutList
import Services.ApiCaller
import Services.SharedPreferenceManager
import Utils.createSnackbar
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers;
import kotlinx.android.synthetic.main.content_calendar.*
import kotlinx.android.synthetic.main.content_calendar.view.*
import kotlinx.android.synthetic.main.recycle_view_calendar.*
import kotlinx.android.synthetic.main.recycle_view_calendar.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WorkoutAdapter(val context: Context) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    val client by lazy { ApiCaller.create() }
    var workouts: ArrayList<Workout> = ArrayList()


    class WorkoutViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): WorkoutViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycle_view_calendar, parent, false)

        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        if(workouts[position].id != null) {
            if(workouts[position].plan == 0) holder.view.header_text.text = "Workout %d".format(workouts[position].id)
            else holder.view.header_text.text = "Plan [trainer %s %s]".format(workouts[position].trainer_client.trainer.user.name, workouts[position].trainer_client.trainer.user.surname)
            holder.view.body_text.text = "Workout added at %s\nExercises: %d\nClick this box to see more info".format(
                SimpleDateFormat("dd-MM-yyyy").format(Date(workouts[position].workout_time_ts.time)), workouts[position].exercises.size)

            holder.view.workout_info.setOnClickListener { v ->
                if(workouts[position].plan > 0) {
                    if(SharedPreferenceManager.getUserID(context) == workouts[position].user_id) {
                        val builder = AlertDialog.Builder(v.rootView.context)
                        builder.setTitle("Workout plan")
                        builder.setMessage("Would you like to copy this plan to a workout?")
                        builder.setPositiveButton("Yes") { _, _ ->
                            client.copyWorkout(workouts[position].id).enqueue(object : Callback<Workout> {
                                override fun onResponse(call: Call<Workout>, response: Response<Workout>) {
                                    if (response.code() == 200) {
                                        val intent = Intent(context, WorkoutActivity::class.java)
                                        intent.putExtra("workoutID", response.body()?.id)
                                        intent.putExtra("tcID", workouts[position].trainer_client_id)
                                        intent.putExtra("userID", workouts[position].user_id)
                                        intent.flags = FLAG_ACTIVITY_NEW_TASK
                                        startActivity(context, intent, null)
                                    }
                                }

                                override fun onFailure(call: Call<Workout>, t: Throwable) {
                                    createSnackbar(v, "Could not copy selected plan - please try again.", Color.RED)
                                }
                            })
                        }
                        builder.setNegativeButton("No") {_, _ -> }
                        builder.show()
                    }
                    else {
                        val intent = Intent(context, WorkoutActivity::class.java)
                        intent.putExtra("workoutID", workouts[position].id)
                        intent.putExtra("tcID", workouts[position].trainer_client_id)
                        intent.putExtra("userID", workouts[position].user_id)
                        intent.flags = FLAG_ACTIVITY_NEW_TASK
                        startActivity(context, intent, null)
                    }
                }
                else {
                    val intent = Intent(context, WorkoutActivity::class.java)
                    intent.putExtra("workoutID", workouts[position].id)
                    intent.putExtra("tcID", workouts[position].trainer_client_id)
                    intent.putExtra("userID", workouts[position].user_id)
                    intent.flags = FLAG_ACTIVITY_NEW_TASK
                    startActivity(context, intent, null)
                }
            }
        }
    }

    override fun getItemCount() = workouts.size

    fun refreshWorkouts(userID: Int, day: Int, month: Int, year: Int, view: View) {
        client.getUserWorkouts(userID).enqueue(object: retrofit2.Callback<WorkoutList> {
            override fun onResponse(call: Call<WorkoutList>, response: Response<WorkoutList>) {
                if (response.code() == 200) {
                    val response_body = response.body()

                    if(response_body?.workouts!!.size <= 0) {
                        println("No workouts")
                    } else {

                        client.getObservableUserWorkouts(userID).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ result ->
                                workouts.clear()
                                for(workout in result.workouts) {
                                    val wday = SimpleDateFormat("dd").format(workout.workout_time_ts).toInt()
                                    val wmonth = SimpleDateFormat("MM").format(workout.workout_time_ts).toInt()
                                    val wyear = SimpleDateFormat("yyyy").format(workout.workout_time_ts).toInt()
                                    if(wyear == year && wmonth == month && wday == day) {
                                        workouts.add(workout)
                                    }
                                }
                                if(workouts.size > 0) view.visibility = View.VISIBLE
                                else view.visibility = View.GONE
                                //workouts.addAll(result.workouts)
                                notifyDataSetChanged()
                            }, { error ->
                                Toast.makeText(context, "Refresh error: ${error.message}", Toast.LENGTH_LONG).show()
                            })
                    }
                }
            }

            override fun onFailure(call: Call<WorkoutList>, t: Throwable) {
                println(t.printStackTrace())
                Toast.makeText(context, t.message.toString(), Toast.LENGTH_LONG).show()
            }
        })
    }
}