package com.example.strengthapp

import Models.JsonResponse
import Models.TrainerClient
import Models.Workout
import Models.WorkoutNote
import Services.ApiCaller
import Services.SharedPreferenceManager
import Utils.createSnackbar
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_workout.*
import kotlinx.android.synthetic.main.content_trainer_profile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Timestamp

class WorkoutActivity : AppCompatActivity() {

    lateinit var adapter: WorkoutExerciseAdapter

    var user_note: WorkoutNote? = null
    var trainer_note: WorkoutNote? = null

    var user = 0
    var tc = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        val client by lazy { ApiCaller.create() }

        var workoutID = intent.getIntExtra("workoutID", SharedPreferenceManager.getCurrentWorkout(applicationContext))
        var tcID = intent.getIntExtra("tcID", 0)
        var userID = intent.getIntExtra("userID", SharedPreferenceManager.getUserID(applicationContext))

        user = userID
        tc = tcID

        SharedPreferenceManager.setCurrentWorkout(applicationContext, workoutID.toInt())

        val call = client.getWorkout(workoutID)
        call.enqueue(object: Callback<Workout> {
            override fun onResponse(call: Call<Workout>, response: Response<Workout>) {
                if(response.code() == 200) {
                    user_note = response.body()?.user_note
                    trainer_note = response.body()?.trainer_note
                }
            }

            override fun onFailure(call: Call<Workout>, t: Throwable) {
            }
        })

        val title = findViewById<TextView>(R.id.title)

        title.text = "Workout %d".format(workoutID)

        adapter = WorkoutExerciseAdapter(this.baseContext)

        w_exercises_list.layoutManager = LinearLayoutManager(this)
        w_exercises_list.adapter = adapter

        adapter.refreshExercises(workoutID)

        backb.setOnClickListener{ v ->
            val intent = Intent(applicationContext, CalendarActivity::class.java)
            intent.putExtra("tcID", tcID)
            intent.putExtra("userID", userID)
            startActivity(intent)
        }

        delete.setOnClickListener { v ->
            val builder = AlertDialog.Builder(v.rootView.context)
            builder.setTitle("Delete workout")
            builder.setMessage("Are you sure you want to delete this workout?")
            builder.setPositiveButton("Yes") {_, _ ->
                client.deleteWorkout(SharedPreferenceManager.getCurrentWorkout(applicationContext)).enqueue(object: Callback<JsonResponse> {
                    override fun onResponse(call: Call<JsonResponse>, response: Response<JsonResponse>) {
                        if(response.code() == 200) {
                            val intent = Intent(applicationContext, CalendarActivity::class.java)
                            intent.putExtra("tcID", tcID)
                            intent.putExtra("userID", userID)
                            startActivity(intent)
                        }
                    }

                    override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                    }
                })
            }

            builder.setNegativeButton("No") {_, _ ->

            }
            builder.show()
        }

        exercise_add_button.setOnClickListener { v ->
            val intent = Intent(v.context, WorkoutExerciseAdd::class.java)
            intent.putExtra("tcID", tcID)
            intent.putExtra("userID", userID)
            startActivity(intent)
        }

        setSupportActionBar(toolbar2)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.workout, menu)
        if(trainer_note == null && SharedPreferenceManager.getTrainerID(applicationContext) == 0) {
            menu.findItem(R.id.action_tnote).setVisible(false)
        }
        else menu.findItem(R.id.action_tnote).setVisible(true)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_note -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Workout note")
                val input = EditText(this)
                input.inputType = InputType.TYPE_CLASS_TEXT
                input.setSingleLine(false)
                if(SharedPreferenceManager.getUserID(applicationContext) != user) input.isClickable = false

                builder.setView(input)
                if(user_note != null) {
                    input.setText(user_note?.note)
                }
                if(SharedPreferenceManager.getUserID(applicationContext) == user) {
                    builder.setPositiveButton("Save") { _, _ ->
                        val client = ApiCaller.create()
                        if (user_note == null) {
                            client.addNewNote(WorkoutNote(0, user, input.text.toString()))
                                .enqueue(object : Callback<WorkoutNote> {
                                    override fun onResponse(call: Call<WorkoutNote>, response: Response<WorkoutNote>) {
                                        if (response.code() == 200) {
                                            client.editWorkout(
                                                SharedPreferenceManager.getCurrentWorkout(applicationContext),
                                                Workout(0, 0, user_note_id = response.body()?.id!!)
                                            ).enqueue(object : Callback<Workout> {
                                                override fun onResponse(
                                                    call: Call<Workout>,
                                                    response: Response<Workout>
                                                ) {
                                                }

                                                override fun onFailure(call: Call<Workout>, t: Throwable) {
                                                }
                                            })
                                        }
                                    }

                                    override fun onFailure(call: Call<WorkoutNote>, t: Throwable) {
                                    }
                                })
                        } else {
                            client.editNote(
                                user_note?.id!!,
                                WorkoutNote(user_note?.id!!, user_note?.user_id!!, input.text.toString())
                            ).enqueue(object : Callback<WorkoutNote> {
                                override fun onResponse(call: Call<WorkoutNote>, response: Response<WorkoutNote>) {
                                }

                                override fun onFailure(call: Call<WorkoutNote>, t: Throwable) {
                                }
                            })
                        }
                    }
                }
                builder.setNegativeButton("Close") {_, _ -> }
                builder.show()
            }
            R.id.action_tnote -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Workout note")
                val input = EditText(this)
                input.inputType = InputType.TYPE_CLASS_TEXT
                input.setSingleLine(false)

                builder.setView(input)
                if(trainer_note != null) {
                    input.setText(trainer_note?.note)
                }
                if(SharedPreferenceManager.getUserID(applicationContext) == trainer_note?.user_id || (trainer_note == null && SharedPreferenceManager.getTrainerID(applicationContext) > 0)) {
                    builder.setPositiveButton("Save") { _, _ ->
                        val client = ApiCaller.create()
                        if (trainer_note == null) {
                            client.addNewNote(WorkoutNote(0, user, input.text.toString()))
                                .enqueue(object : Callback<WorkoutNote> {
                                    override fun onResponse(call: Call<WorkoutNote>, response: Response<WorkoutNote>) {
                                        if(response.code() == 200) {
                                            client.editWorkout(SharedPreferenceManager.getCurrentWorkout(applicationContext),
                                                Workout(0, 0, trainer_note_id = response.body()?.id!!)).enqueue(object: Callback<Workout> {
                                                override fun onResponse(call: Call<Workout>, response: Response<Workout>) {
                                                }
                                                override fun onFailure(call: Call<Workout>, t: Throwable) {
                                                }
                                            })
                                        }
                                    }

                                    override fun onFailure(call: Call<WorkoutNote>, t: Throwable) {
                                    }
                                })
                        } else {
                            client.editNote(
                                trainer_note?.id!!,
                                WorkoutNote(trainer_note?.id!!, trainer_note?.user_id!!, input.text.toString())
                            ).enqueue(object : Callback<WorkoutNote> {
                                override fun onResponse(call: Call<WorkoutNote>, response: Response<WorkoutNote>) {
                                }

                                override fun onFailure(call: Call<WorkoutNote>, t: Throwable) {
                                }
                            })
                        }
                    }
                }
                builder.setNegativeButton("Close") {_, _ -> }
                builder.show()
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }
}
