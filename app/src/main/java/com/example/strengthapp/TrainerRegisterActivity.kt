package com.example.strengthapp

import Models.JsonTrainerResponse
import Models.Trainer
import Services.ApiCaller
import Services.SharedPreferenceManager
import Utils.createSnackbar
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_change_password.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrainerRegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trainer_register)

        val client by lazy { ApiCaller.create() }

        val certificate = findViewById<TextView>(R.id.certificate_input)
        val website = findViewById<TextView>(R.id.website_input)
        val about_me = findViewById<TextView>(R.id.about_me_input)

        val button = findViewById<Button>(R.id.register)

        if(SharedPreferenceManager.getLoggedStatus(applicationContext)) {
            backb.setOnClickListener{ v ->
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            }

            button.setOnClickListener { v ->
                val intent = Intent(applicationContext, HomeActivity::class.java)

                if(certificate.text.isNotEmpty()) {
                    val call = client.addTrainer(Trainer(0, SharedPreferenceManager.getUserID(applicationContext),
                        certificate.text.toString(), website.text.toString(), about_me.text.toString()))

                    call.enqueue(object : Callback<JsonTrainerResponse> {
                        override fun onResponse(call: Call<JsonTrainerResponse>, response: Response<JsonTrainerResponse>) {
                            if(response.code() == 200) {
                                val response_body = response.body()


                                if(response_body?.message?.length!! > 1) {
                                    createSnackbar(v, response_body.message, Color.RED)
                                }
                                else {
                                    SharedPreferenceManager.setTrainerID(applicationContext, response_body.id)
                                    SharedPreferenceManager.setLoggedIn(applicationContext, true)

                                    val builder = AlertDialog.Builder(v.context)
                                    builder.setTitle("Successful trainer registration")
                                    builder.setMessage("Registered as a trainer - welcome to the application.")

                                    builder.setPositiveButton("Continue") { _, _ ->
                                        startActivity(intent)
                                    }
                                    builder.show()
                                }
                            }
                        }

                        override fun onFailure(call: Call<JsonTrainerResponse>, t: Throwable) {
                            createSnackbar(v, t.message.toString(), Color.RED)
                        }
                    })
                }
                else {
                    createSnackbar(v, "Certificate is a required field - it mustn't be empty.", Color.RED)
                }
            }
        }
        else {
            val intent = Intent(applicationContext, MainActivity::class.java)
            createSnackbar(button, "Unrecognized user account - please try registering again.", Color.RED)
            startActivity(intent)
        }
    }
}
