package com.example.strengthapp

import Models.TrainerReview
import Services.ApiCaller
import Utils.createSnackbar
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_review_view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_view)

        val client by lazy { ApiCaller.create() }

        client.getReview(intent.getIntExtra("reviewID", 0)).enqueue(object: Callback<TrainerReview> {
            override fun onResponse(call: Call<TrainerReview>, response: Response<TrainerReview>) {
                if(response.code() == 200) {
                    client_text.setText("%s %s".format(response.body()?.trainerClient?.client?.name, response.body()?.trainerClient?.client?.surname))
                    date_text.setText("%s".format(response.body()?.review_add_ts.toString()))
                    text_text.setText("%s".format(response.body()?.review_text))
                }
            }

            override fun onFailure(call: Call<TrainerReview>, t: Throwable) {
                createSnackbar(client_card, "Something went wrong - returning back...", Color.RED)
                val intent = Intent(applicationContext, ReviewListActivity::class.java)
                intent.putExtra("trainerID", intent.getIntExtra("trainerID", 0))
                intent.putExtra("userID", intent.getIntExtra("userID", 0))
                startActivity(intent)
            }
        })

        backb.setOnClickListener { v ->
            val intent = Intent(applicationContext, ReviewListActivity::class.java)
            intent.putExtra("trainerID", intent.getIntExtra("trainerID", 0))
            intent.putExtra("userID", intent.getIntExtra("userID", 0))
            startActivity(intent)
        }
    }
}
