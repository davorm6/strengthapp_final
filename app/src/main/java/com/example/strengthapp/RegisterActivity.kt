package com.example.strengthapp

import Models.JsonResponse
import Models.User
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val client by lazy { ApiCaller.create() }

        val name = findViewById<TextView>(R.id.name_input)
        val surname = findViewById<TextView>(R.id.surname_input)
        val email = findViewById<TextView>(R.id.e_mail_input)
        val password = findViewById<TextView>(R.id.password_input)
        val repeat_password = findViewById<TextView>(R.id.password_repeat_input)

        val button = findViewById<Button>(R.id.register)
        val trainer_button = findViewById<Button>(R.id.register_trainer)

        if(SharedPreferenceManager.getLoggedStatus(applicationContext)) {
            if(SharedPreferenceManager.getUserID(applicationContext) > 0) {
                val intent = Intent(applicationContext, HomeActivity::class.java)
                createSnackbar(button, "Already logged in", Color.GREEN)
                Handler().postDelayed(Runnable {
                    startActivity(intent)
                }, 2000)
            }
        }

        button.setOnClickListener { v ->
            val completionIntent = Intent(v.context, HomeActivity::class.java)

            if(password.text.toString() == repeat_password.text.toString()) {
                if(name.text.isNotEmpty() && surname.text.isNotEmpty() && email.text.isNotEmpty()) {
                    val call = client.addUser(User(0, email.text.toString(), password.text.toString(),
                        name.text.toString(), surname.text.toString()
                    ))

                    call.enqueue(object : Callback<JsonResponse> {
                        override fun onResponse(call: Call<JsonResponse>, response: Response<JsonResponse>) {
                            if(response.code() == 200) {
                                val response_body = response.body()


                                if(response_body?.message?.length!! > 1) {
                                    createSnackbar(v, response_body.message, Color.RED)
                                }
                                else {
                                    SharedPreferenceManager.setLoggedIn(applicationContext, true)
                                    SharedPreferenceManager.setUserID(applicationContext, response_body.id)
                                    SharedPreferenceManager.setTrainerID(applicationContext, 0)
                                    SharedPreferenceManager.setAdmin(applicationContext, false)

                                    val builder = AlertDialog.Builder(v.context)
                                    builder.setTitle("Successful registration")
                                    builder.setMessage("Registered with e-mail address %s.".format(response_body.e_mail))

                                    builder.setPositiveButton("Continue") { _, _ ->
                                        startActivity(completionIntent)
                                    }
                                    builder.setOnCancelListener { v ->
                                        startActivity(completionIntent)
                                    }
                                    builder.show()
                                }
                            }
                        }

                        override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                            createSnackbar(v, t.message.toString(), Color.RED)
                        }
                    })
                }
                else {
                    createSnackbar(v, "Required fields must not be empty", Color.RED)
                }
            }
            else {
                createSnackbar(v, "Entered passwords do not match (%s - %s".format(password.text.toString(), repeat_password.text.toString()), Color.RED)
            }
        }

        trainer_button.setOnClickListener { v ->
            val completionIntent = Intent(v.context, TrainerRegisterActivity::class.java)

            if(password.text.toString() == repeat_password.text.toString()) {
                if(name.text.isNotEmpty() && surname.text.isNotEmpty() && email.text.isNotEmpty()) {
                    val call = client.addUser(User(0, email.text.toString(), password.text.toString(),
                        name.text.toString(), surname.text.toString()
                    ))

                    call.enqueue(object : Callback<JsonResponse> {
                        override fun onResponse(call: Call<JsonResponse>, response: Response<JsonResponse>) {
                            if(response.code() == 200) {
                                val response_body = response.body()


                                if(response_body?.message?.length!! > 1) {
                                    createSnackbar(v, response_body.message, Color.RED)
                                }
                                else {
                                    SharedPreferenceManager.setLoggedIn(applicationContext, true)
                                    SharedPreferenceManager.setUserID(applicationContext, response_body.id)

                                    val builder = AlertDialog.Builder(v.context)
                                    builder.setTitle("Created new user")
                                    builder.setMessage("Registered with e-mail address %s\nYou will now be taken to trainer registration form.".format(response_body.e_mail))

                                    builder.setPositiveButton("Continue") { _, _ ->
                                        startActivity(completionIntent)
                                    }
                                    builder.show()
                                }
                            }
                        }

                        override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                            createSnackbar(v, t.message.toString(), Color.RED)
                        }
                    })
                }
                else {
                    createSnackbar(v, "Required fields must not be empty", Color.RED)
                }
            }
            else {
                createSnackbar(v, "Entered passwords do not match", Color.RED)
            }
        }
    }
}
