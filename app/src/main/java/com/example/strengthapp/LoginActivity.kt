package com.example.strengthapp

import Models.JsonResponse
import Models.Trainer
import Models.TrainerClient
import Services.ApiCaller
import Services.SharedPreferenceManager
import Utils.createSnackbar
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val client by lazy { ApiCaller.create() }

        val login_button = findViewById<Button>(R.id.login_button)
        val email = findViewById<TextView>(R.id.e_mail_input)
        val password = findViewById<TextView>(R.id.password_input)

        if(SharedPreferenceManager.getLoggedStatus(applicationContext)) {
            if(SharedPreferenceManager.getUserID(applicationContext) > 0) {
                val intent = Intent(applicationContext, HomeActivity::class.java)
                createSnackbar(login_button, "Already logged in", Color.GREEN)
                Handler().postDelayed(Runnable {
                    startActivity(intent)
                }, 2000)
            }
        }

        login_button.setOnClickListener { v ->
            val intent = Intent(v.context, HomeActivity::class.java)

            if(email.text.isNotEmpty() && password.text.isNotEmpty()) {
                val json: JsonObject = JsonObject()
                json.addProperty("mail", email.text.toString())
                json.addProperty("password", password.text.toString())

                val call = client.loginUser(json)

                call.enqueue(object: Callback<JsonResponse> {
                    override fun onResponse(call: Call<JsonResponse>, response: Response<JsonResponse>) {
                        if(response.code() == 200) {
                            val response_body = response.body()

                            if (response_body?.message?.contains("Accept")!!) {
                                SharedPreferenceManager.setLoggedIn(applicationContext, true)
                                SharedPreferenceManager.setUserID(applicationContext, response_body.id)
                                System.out.println(response.body())
                                if(response_body.admin > 0) {
                                    SharedPreferenceManager.setAdmin(applicationContext, true)
                                }
                                else {
                                    SharedPreferenceManager.setAdmin(applicationContext, false)
                                }

                                val call2 = client.getUserTrainer(response_body.id)
                                call2.enqueue(object: Callback<Trainer> {
                                    override fun onResponse(call: Call<Trainer>, response: Response<Trainer>) {
                                        if(response.code() == 200) SharedPreferenceManager.setTrainerID(applicationContext, response.body()?.id!!)

                                        val call3 = client.getUserTrainerC(response_body.id)
                                        call3.enqueue(object: Callback<TrainerClient> {
                                            override fun onResponse(
                                                call: Call<TrainerClient>,
                                                response: Response<TrainerClient>
                                            ) {
                                                System.out.println(response.body())
                                                System.out.println(response.code())
                                                if(response.code() == 200) {
                                                    SharedPreferenceManager.setTrainerCID(applicationContext, response.body()?.id!!.toInt())
                                                    SharedPreferenceManager.setUserTrainerID(applicationContext, response.body()?.trainer_id!!.toInt())
                                                    System.out.println(SharedPreferenceManager.getTrainerCID(applicationContext))
                                                    System.out.println(SharedPreferenceManager.getUserTrainerID(applicationContext))
                                                }

                                                val builder = AlertDialog.Builder(v.context)
                                                builder.setTitle("Successful login")
                                                builder.setMessage("You will now be taken to the main screen.")

                                                builder.setPositiveButton("Continue") { _, _ ->
                                                    startActivity(intent)
                                                }
                                                builder.setOnCancelListener { v ->
                                                    startActivity(intent)
                                                }
                                                builder.show()
                                            }

                                            override fun onFailure(call: Call<TrainerClient>, t: Throwable) {

                                            }
                                        })

                                    }

                                    override fun onFailure(call: Call<Trainer>, t: Throwable) {
                                    }
                                })


                            } else {
                                createSnackbar(v, response_body.message, Color.RED)
                            }
                        }
                    }
                    override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                        createSnackbar(v, t.message.toString(), Color.RED)
                    }

                })
            }
            else {
                createSnackbar(v, "E-mail or password field must not be empty.", Color.RED)
            }
        }
    }
}
