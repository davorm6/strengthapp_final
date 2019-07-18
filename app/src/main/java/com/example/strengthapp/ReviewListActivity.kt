package com.example.strengthapp

import Models.Notification
import Models.TrainerClientList
import Models.TrainerReview
import Services.ApiCaller
import Services.SharedPreferenceManager
import Utils.createSnackbar
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_review_list.*
import kotlinx.android.synthetic.main.content_trainer.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewListActivity : AppCompatActivity() {

    lateinit var adapter: ReviewAdapter

    var tcid: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_list)

        val client by lazy { ApiCaller.create() }

        adapter = ReviewAdapter(this.baseContext)

        reviews.layoutManager = LinearLayoutManager(this)
        reviews.adapter = adapter

        val trainer = intent.getIntExtra("trainerID", 0)
        val user_id = intent.getIntExtra("userID", 0)

        adapter.refreshReviews(trainer)

        backb.setOnClickListener { v ->
            val intent = Intent(applicationContext, TrainerProfileActivity::class.java)
            intent.putExtra("trainerID", trainer)
            intent.putExtra("userID", intent.getIntExtra("userID", 0))
            startActivity(intent)
        }

        add_review.visibility = View.GONE

        add_review.setOnClickListener { v ->
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Add review")
            builder.setMessage("Write your review for this coach")
            val input = EditText(v.context)
            input.inputType = InputType.TYPE_CLASS_TEXT
            input.setSingleLine(false)

            builder.setView(input)
            builder.setPositiveButton("Add") {_, _ ->
                if(input.text.length >= 180) {
                    client.addNotification(
                        Notification(
                            0,
                            user_id.toLong(),
                            "One of your clients has added a new review of you."
                        )
                    ).enqueue(object : Callback<Notification> {
                        override fun onResponse(call: Call<Notification>, response: Response<Notification>) {
                            if (response.code() == 200) {
                                client.addNewReview(
                                    TrainerReview(
                                        0,
                                        tcid!!.toInt(),
                                        input.text.toString(),
                                        notification_id = response.body()?.id!!.toInt()
                                    )
                                ).enqueue(object : Callback<TrainerReview> {
                                    override fun onResponse(
                                        call: Call<TrainerReview>,
                                        response: Response<TrainerReview>
                                    ) {
                                        adapter.refreshReviews(trainer)
                                    }

                                    override fun onFailure(call: Call<TrainerReview>, t: Throwable) {
                                    }
                                })
                            }
                        }

                        override fun onFailure(call: Call<Notification>, t: Throwable) {
                        }
                    })
                }
                else {
                    createSnackbar(add_review, "Your review has to be at least 180 characters long - missing %d.".format(180-input.text.length), Color.RED)
                }

            }
            builder.setNegativeButton("Cancel") {_, _ -> }
            builder.show()
        }

        client.getUserTrainers(SharedPreferenceManager.getUserID(applicationContext)).enqueue(object:
            Callback<TrainerClientList> {
            override fun onResponse(call: Call<TrainerClientList>, response: Response<TrainerClientList>) {
                if(response.code() == 200) {
                    var list = response.body()?.trainer_clients
                    for(t in list!!) {
                        if(t.trainer_id == trainer.toLong()) {
                            add_review.visibility = View.VISIBLE
                            tcid = t.id
                        }
                    }
                }
            }

            override fun onFailure(call: Call<TrainerClientList>, t: Throwable) {
            }
        })
    }
}
