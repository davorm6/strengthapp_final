package com.example.strengthapp

import Models.Trainer
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

class TrainerAdapter(val context: Context) : RecyclerView.Adapter<TrainerAdapter.TrainerViewHolder>() {

    val client by lazy { ApiCaller.create() }
    var trainers: ArrayList<Trainer> = ArrayList()

    init { refreshTrainers() }

    class TrainerViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): TrainerViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item, parent, false)

        return TrainerViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrainerViewHolder, position: Int) {
        println("Ovdje sam bio - bindViewHolder")
        holder.view.name.text = "%s %s".format(trainers[position].user.name, trainers[position].user.surname)
        if(trainers[position].profile_photo.isNotEmpty()) {
            holder.view.avatar.setImageBitmap(bytesToBitmap(trainers[position].profile_photo))
        }

        holder.view.trainer_card.setOnClickListener { v ->
            val intent = Intent(context, TrainerProfileActivity::class.java)
            intent.putExtra("userID", trainers[position].user_id)
            intent.putExtra("trainerID", trainers[position].id)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            ContextCompat.startActivity(context, intent, null)
        }
    }

    override fun getItemCount() = trainers.size

    fun refreshTrainers() {
        client.getTrainers()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                trainers.clear()
                trainers.addAll(result.trainers)
                notifyDataSetChanged()
            },{ error ->
                Toast.makeText(context, "Refresh error: ${error.message}", Toast.LENGTH_LONG).show()
            })
    }
}