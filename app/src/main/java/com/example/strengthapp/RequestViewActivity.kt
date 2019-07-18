package com.example.strengthapp

import Models.Exercise
import Models.JsonResponse
import Models.Notification
import Services.ApiCaller
import Services.SharedPreferenceManager
import Utils.createSnackbar
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_exercise_view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RequestViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_view)

        val client by lazy { ApiCaller.create() }

        val exercise_id = intent.extras["exerciseID"]
        var user: Long? = null

        val call = client.getExercise(exercise_id as Int)

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
                    user = body?.request.toLong()

                }
                else {
                    createSnackbar(exercise_title, "Something went wrong, please try again.", Color.RED)
                    val intent = Intent(applicationContext, RequestReviewActivity::class.java)
                    startActivity(intent)
                }
            }
        })

        if(!SharedPreferenceManager.getAdmin(applicationContext)) {
            val intent = Intent(applicationContext, HomeActivity::class.java)
            startActivity(intent)
        }
        else {
            approve.visibility = View.VISIBLE
            deny.visibility = View.VISIBLE

            approve.setOnClickListener { v ->
                val call = client.approveRequest(exercise_id as Int)
                call.enqueue(object : Callback<JsonResponse> {
                    override fun onResponse(call: Call<JsonResponse>, response: Response<JsonResponse>) {
                        if(response.code() == 200) {
                            createSnackbar(v, response.body()?.message!!, Color.RED)
                            val intent = Intent(applicationContext, RequestReviewActivity::class.java)
                            startActivity(intent)
                        }
                    }

                    override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                        createSnackbar(v, t.message.toString(), Color.RED)
                    }
                })
            }

            deny.setOnClickListener { v ->
                val builder = AlertDialog.Builder(v.context)
                builder.setTitle("Deny request")
                builder.setMessage("Please enter a reason sent to user for denying this request.")

                val input = EditText(v.context)
                input.inputType = InputType.TYPE_CLASS_TEXT

                builder.setView(input)

                builder.setPositiveButton("Deny") { _, _ ->
                    val call_n = client.addNotification(Notification(0, user,"Administrator denied your request for exercise " + exercise_title.text + ". Reason: " + input.text + "."))
                    call_n.enqueue(object: Callback<Notification> {
                        override fun onResponse(call: Call<Notification>, response: Response<Notification>) {
                        }

                        override fun onFailure(call: Call<Notification>, t: Throwable) {
                        }
                    })

                    val call = client.deleteExercise(exercise_id as Int)
                    call.enqueue(object : Callback<JsonResponse> {
                        override fun onResponse(call: Call<JsonResponse>, response: Response<JsonResponse>) {
                            if (response.code() == 200) {
                                createSnackbar(v, response.body()?.message!!, Color.RED)
                                val intent = Intent(applicationContext, RequestReviewActivity::class.java)
                                startActivity(intent)
                            }
                        }

                        override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                            createSnackbar(v, t.message.toString(), Color.RED)
                        }
                    })
                }

                builder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }
                builder.show()
            }

            backb.setOnClickListener { v ->
                val intent = Intent(applicationContext, AdminActivity::class.java)
                startActivity(intent)
            }
        }
    }
}