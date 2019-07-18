package com.example.strengthapp

import Models.Muscle
import Models.Trainer
import Models.Type
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
import io.reactivex.android.schedulers.AndroidSchedulers;
import kotlinx.android.synthetic.main.recycler_view_client.view.*

class MuscleAdapter(val context: Context) : RecyclerView.Adapter<MuscleAdapter.MuscleViewHolder>() {

    val client by lazy { ApiCaller.create() }
    var muscles: ArrayList<Muscle> = ArrayList()

    init { refreshMuscles() }

    class MuscleViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MuscleViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_client, parent, false)

        return MuscleViewHolder(view)
    }

    override fun onBindViewHolder(holder: MuscleViewHolder, position: Int) {
        println("Ovdje sam bio - bindViewHolder")
        holder.view.client_card.client_name.text = muscles[position].name
    }

    override fun getItemCount() = muscles.size

    fun refreshMuscles() {
        client.getMusclesO()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                muscles.clear()
                muscles.addAll(result.muscles)
                notifyDataSetChanged()
            },{ error ->
                Toast.makeText(context, "Refresh error: ${error.message}", Toast.LENGTH_LONG).show()
            })
    }
}