package com.example.strengthapp

import Models.Trainer
import Models.TrainerReview
import Services.ApiCaller
import Utils.bytesToBitmap
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.recycler_view_item.view.*
import io.reactivex.android.schedulers.AndroidSchedulers;
import kotlinx.android.synthetic.main.recycler_view_review.view.*

class ReviewAdapter(val context: Context) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    val client by lazy { ApiCaller.create() }
    var reviews: ArrayList<TrainerReview> = ArrayList()

    init { refreshReviews(0) }

    class ReviewViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ReviewViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_review, parent, false)

        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        println("Ovdje sam bio - bindViewHolder")
        holder.view.review_card.date.text = "%s - %s".format(reviews[position].trainerClient.request_response_ts.toString(), if(reviews[position].trainerClient.end_time_ts == null) "still client" else reviews[position].trainerClient.end_time_ts.toString())
        holder.view.review_card.text.text = "Client %s %s".format(reviews[position].trainerClient.client.name, reviews[position].trainerClient.client.surname)

        holder.view.review_card.setOnClickListener { v ->
            val intent = Intent(context, ReviewViewActivity::class.java)
            intent.putExtra("reviewID", reviews[position].id)
            intent.putExtra("trainerID", reviews[position].trainerClient.trainer_id)
            intent.putExtra("userID", reviews[position].trainerClient.trainer.user_id)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            ContextCompat.startActivity(context, intent, null)
        }
    }

    override fun getItemCount() = reviews.size

    fun refreshReviews(id: Int) {
        client.getTrainerReviews(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                reviews.clear()
                reviews.addAll(result.reviews)
                reviews.sortBy { it.review_add_ts }
                reviews.reverse()
                notifyDataSetChanged()
            },{ error ->
                Toast.makeText(context, "Refresh error: ${error.message}", Toast.LENGTH_LONG).show()
            })
    }
}