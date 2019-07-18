package com.example.strengthapp

import Services.ApiCaller
import Services.SharedPreferenceManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_workout_exercise_add.*
import kotlinx.android.synthetic.main.activity_workout_exercise_add.backb

class WorkoutExerciseAdd : AppCompatActivity() {

    lateinit var adapter: ExerciseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_exercise_add)

        val client by lazy { ApiCaller.create() }

        adapter = ExerciseAdapter(this.baseContext)

        search_results.layoutManager = LinearLayoutManager(this)
        search_results.adapter = adapter

        adapter.refreshSearch("")

        val user = intent.getIntExtra("userID", 0)
        val tc = intent.getIntExtra("tcID", 0)

        backb.setOnClickListener{ v ->
            val intent = Intent(applicationContext, WorkoutActivity::class.java)
            intent.putExtra("workoutID", SharedPreferenceManager.getCurrentWorkout(applicationContext))
            intent.putExtra("userID", user)
            intent.putExtra("tcID", tc)
            startActivity(intent)
        }

        search_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(search_input.text?.isNotEmpty()!!) {
                    adapter.refreshSearch(search_input.text.toString())
                }
            }
        })
    }
}
