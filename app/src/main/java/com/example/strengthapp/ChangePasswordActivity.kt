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
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_change_password.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        val client by lazy { ApiCaller.create() }

        var user: Int = SharedPreferenceManager.getUserID(applicationContext)
        if(intent.hasExtra("id")) {
            user = intent.extras["id"].toString().toInt()
        }
        var user_id = SharedPreferenceManager.getUserID(applicationContext)
        if(user != null) {
            user_id = user.toString().toInt()
        }

        var name: String? = ""
        var surname: String? = ""
        var mail: String? = ""
        var password: String? = ""

        var changes = 0

        val call2 = client.getUser(user_id)
        call2.enqueue(object: Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.code() == 200) {
                    val body = response.body()

                    name = body?.name
                    surname = body?.surname
                    mail = body?.e_mail
                    password = body?.password
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                createSnackbar(backb, t.message.toString(), Color.RED)
            }
        })

        backb.setOnClickListener{ v ->
            val intent = Intent(applicationContext, ProfileActivity::class.java)
            startActivity(intent)
        }

        save.setOnClickListener { v ->
            if(pass.text?.isNotEmpty()!! && newpass.text?.isNotEmpty()!! && repeatnewpass.text?.isNotEmpty()!!) {
                if(pass.text.toString() == password) {
                    if(newpass.text.toString() == repeatnewpass.text.toString()) {
                        val call3 =
                            client.updateUser(user_id, User(0, mail!!, newpass.text.toString(), name!!, surname!!))

                        call3.enqueue(object : Callback<JsonResponse> {
                            override fun onResponse(call: Call<JsonResponse>, response: Response<JsonResponse>) {
                                if (response.code() == 200) {
                                    val builder = AlertDialog.Builder(v.context)
                                    builder.setTitle("Successful password change")
                                    builder.setMessage("Your new password is '${newpass.text.toString()}', do not forget it.")

                                    builder.setPositiveButton("Ok") { _, _ ->
                                        val intent = Intent(applicationContext, ProfileActivity::class.java)
                                        startActivity(intent)
                                    }
                                    builder.show()
                                }
                            }

                            override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                                createSnackbar(v, t.message.toString(), Color.RED)
                            }

                        })
                    }
                    else {
                        createSnackbar(v, "Passwords do not match - please try again.", Color.RED)
                    }
                }
                else {
                    createSnackbar(v, "You entered the wrong password - please try again.", Color.RED)
                }
            }
            else {
                createSnackbar(v, "Fields must not be empty - please try again.", Color.RED)
            }
        }
    }
}
