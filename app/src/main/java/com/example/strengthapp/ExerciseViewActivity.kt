package com.example.strengthapp

import Models.Exercise
import Services.ApiCaller
import Utils.createSnackbar
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_exercise_view.*
import kotlinx.android.synthetic.main.activity_exercise_view.backb
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExerciseViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_view)

        val client by lazy { ApiCaller.create() }

        val exerciseID = intent.extras["exerciseID"]

        backb.setOnClickListener{ v ->
            val intent = Intent(applicationContext, ExerciseListActivity::class.java)
            startActivity(intent)
        }

        val call = client.getExercise(exerciseID as Int)

        call.enqueue(object: Callback<Exercise> {
            override fun onFailure(call: Call<Exercise>, t: Throwable) {
                createSnackbar(exercise_title, t.message.toString(), Color.RED)
                val intent = Intent(applicationContext, ExerciseListActivity::class.java)
                startActivity(intent)
            }

            override fun onResponse(call: Call<Exercise>, response: Response<Exercise>) {
                if(response.code() == 200) {
                    val body = response.body()

                    exercise_title.text = body?.name
                    e_type_text.setText(body?.type?.name)
                    val muscles : ArrayList<String> = ArrayList<String>()
                    for(muscle in body?.muscles!!) {
                        muscles.add(muscle.name)
                    }
                    e_muscle_text.setText(muscles.joinToString(","))
                    e_info_text.setText(body?.information)
                    e_ins_text.setText(body?.instructions)

                }
                else {
                    createSnackbar(exercise_title, "Something went wrong, please try again.", Color.RED)
                    val intent = Intent(applicationContext, ExerciseListActivity::class.java)
                    startActivity(intent)
                }
            }
        })
    }
}
