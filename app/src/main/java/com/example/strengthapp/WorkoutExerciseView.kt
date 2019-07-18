package com.example.strengthapp

import Models.JsonResponse
import Services.ApiCaller
import Services.SharedPreferenceManager
import Utils.createSnackbar
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_workout_exercise_view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WorkoutExerciseView : AppCompatActivity() {

    lateinit var adapter: ExerciseSetAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_exercise_view)

        val client by lazy { ApiCaller.create() }

        adapter = ExerciseSetAdapter(this.baseContext)

        sets_list.layoutManager = LinearLayoutManager(this)
        sets_list.adapter = adapter

        val exercise_id = intent.extras["exerciseID"]

        adapter.refreshSets(SharedPreferenceManager.getCurrentWorkout(applicationContext), exercise_id as Int)

        we_title.text = intent.extras["exerciseName"].toString()

        backb.setOnClickListener{ v ->
            val intent = Intent(applicationContext, WorkoutActivity::class.java)
            intent.putExtra("workoutID", SharedPreferenceManager.getCurrentWorkout(applicationContext))
            startActivity(intent)
        }

        add_sets.setOnClickListener { v ->
            val json: JsonObject = JsonObject()
            val jsoninside: JsonObject = JsonObject()
            jsoninside.addProperty("workout_id", SharedPreferenceManager.getCurrentWorkout(applicationContext))
            jsoninside.addProperty("exercise_id", exercise_id)
            json.add("workoutExerciseId", jsoninside)
            json.addProperty("set_number", adapter.maxSet() + 1)

            val resultjson: JsonObject = JsonObject()
            resultjson.add("exerciseSetId", json)

            val call = client.addWorkoutExerciseSet(resultjson)

            call.enqueue(object: Callback<JsonResponse> {
                override fun onResponse(call: Call<JsonResponse>, response: Response<JsonResponse>) {
                    if(response.code() == 200) {
                        val response_body = response.body()

                        if (response_body?.message?.length!! <= 1) {
                            adapter.refreshSets(SharedPreferenceManager.getCurrentWorkout(applicationContext), exercise_id as Int)
                        } else {
                            createSnackbar(v, response_body.message.toString(), Color.RED)
                        }
                    }
                }
                override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                    createSnackbar(v, t.message.toString(), Color.RED)
                }
            })
        }

        remove.setOnClickListener { v ->
            val builder = AlertDialog.Builder(v.context)
            builder.setTitle("Remove workout")
            builder.setMessage("Are you sure you want to delete this workout?")

            builder.setPositiveButton("Yes") { _, _ ->
                val call2 = client.deleteWorkoutExercise(SharedPreferenceManager.getCurrentWorkout(applicationContext), exercise_id as Int)
                call2.enqueue(object: Callback<JsonResponse> {
                    override fun onResponse(call: Call<JsonResponse>, response: Response<JsonResponse>) {
                        if(response.code() == 200) {
                            createSnackbar(v, response.body()?.message!!, Color.RED)
                            val intent = Intent(applicationContext, WorkoutActivity::class.java)
                            intent.putExtra("workoutID", SharedPreferenceManager.getCurrentWorkout(applicationContext))
                            startActivity(intent)
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

        adapter.refreshSets(SharedPreferenceManager.getCurrentWorkout(applicationContext), exercise_id as Int)
    }

    override fun onResume() {
        super.onResume()

        if(intent.hasExtra("exerciseID")) adapter.refreshSets(SharedPreferenceManager.getCurrentWorkout(applicationContext), intent.extras["exerciseID"] as Int)
    }
}
