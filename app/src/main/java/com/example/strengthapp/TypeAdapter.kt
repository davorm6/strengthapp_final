package com.example.strengthapp

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
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers;
import kotlinx.android.synthetic.main.recycler_view_client.view.*

class TypeAdapter(val context: Context) : RecyclerView.Adapter<TypeAdapter.TypeViewHolder>() {

    val client by lazy { ApiCaller.create() }
    var types: ArrayList<Type> = ArrayList()

    init { refreshTypes() }

    class TypeViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): TypeViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_client, parent, false)

        return TypeViewHolder(view)
    }

    override fun onBindViewHolder(holder: TypeViewHolder, position: Int) {
        println("Ovdje sam bio - bindViewHolder")
        holder.view.client_card.client_name.text = types[position].name
    }

    override fun getItemCount() = types.size

    fun refreshTypes() {
        client.getTypesO()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                types.clear()
                types.addAll(result.types)
                notifyDataSetChanged()
            },{ error ->
                Toast.makeText(context, "Refresh error: ${error.message}", Toast.LENGTH_LONG).show()
            })
    }
}