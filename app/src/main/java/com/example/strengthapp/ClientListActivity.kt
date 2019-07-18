package com.example.strengthapp

import Models.JsonResponse
import Models.TrainerClient
import Services.ApiCaller
import Services.SharedPreferenceManager
import Utils.createSnackbar
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_client_list.*
import android.graphics.Color
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ClientListActivity : AppCompatActivity() {

    lateinit var adapter: ClientAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_list)

        val client by lazy { ApiCaller.create() }

        backb.setOnClickListener { v ->
            val intent = Intent(applicationContext, TrainerProfileActivity::class.java)
            startActivity(intent)
        }

        adapter = ClientAdapter(this.baseContext)

        clients.layoutManager = LinearLayoutManager(this)
        clients.adapter = adapter

        adapter.refreshClients(SharedPreferenceManager.getTrainerID(applicationContext))

        client_add.setOnClickListener { v ->
            val builder = AlertDialog.Builder(v.context)
            builder.setTitle("Add client")
            builder.setMessage("Please enter clients username (e_mail).")

            val input = EditText(v.context)
            input.inputType = InputType.TYPE_CLASS_TEXT

            builder.setView(input)

            builder.setPositiveButton("Add") { _, _ ->
                val json: JsonObject = JsonObject()
                json.addProperty("mail", input.text.toString() + "@")
                val call2 = client.getUserByMail(json)
                call2.enqueue(object: Callback<JsonResponse> {
                    override fun onResponse(call: Call<JsonResponse>, response: Response<JsonResponse>) {
                        if(response.code() == 200) {
                            if(response.body()?.message!!.length > 1) {
                                createSnackbar(v, response.body()?.message!!, Color.RED)
                            }
                            else {
                                val call3 = client.addTrainerClient(TrainerClient(0, SharedPreferenceManager.getTrainerID(applicationContext).toLong(), response.body()?.id!!.toLong()))
                                call3.enqueue(object: Callback<TrainerClient> {
                                    override fun onResponse(
                                        call: Call<TrainerClient>,
                                        response: Response<TrainerClient>
                                    ) {
                                        if(response.code() == 200) {
                                            createSnackbar(v, "Request sent to client.", Color.GREEN)
                                        }
                                    }

                                    override fun onFailure(call: Call<TrainerClient>, t: Throwable) {
                                        createSnackbar(v, t.message.toString(), Color.RED)
                                    }
                                })
                            }
                        }
                    }
                    override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                        createSnackbar(v, t.message.toString(), Color.RED)
                    }
                })
            }
            builder.setNegativeButton("Cancel", {dialog, _ ->
                dialog.cancel()
            })
            builder.show()
        }
    }
}
