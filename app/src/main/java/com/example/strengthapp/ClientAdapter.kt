package com.example.strengthapp

import Models.JsonResponse
import Models.TrainerClient
import Services.ApiCaller
import Utils.createSnackbar
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers;
import kotlinx.android.synthetic.main.recycler_view_client.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Timestamp
import kotlin.collections.ArrayList

class ClientAdapter(val context: Context) : RecyclerView.Adapter<ClientAdapter.ClientViewHolder>() {

    val client by lazy { ApiCaller.create() }
    var clients: ArrayList<TrainerClient> = ArrayList()
    var requests: ArrayList<TrainerClient> = ArrayList()

    init { refreshClients(0) }

    class ClientViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ClientViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_client, parent, false)

        return ClientViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        println("Ovdje sam bio - bindViewHolder")
        holder.view.client_name.text = "%s %s".format(clients[position].client.name, clients[position].client.surname)

        holder.view.client_card.setOnClickListener { v ->
            val intent = Intent(context, CalendarActivity::class.java)
            intent.putExtra("userID", clients[position].client.id.toInt())
            intent.putExtra("tcID", clients[position].id.toInt())
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            ContextCompat.startActivity(context, intent, null)
        }

        holder.view.client_card.setOnLongClickListener { v ->
            val builder = AlertDialog.Builder(v.rootView.context)
            builder.setTitle("Remove client")
            builder.setMessage("Are you sure you want to remove this client?")

            builder.setPositiveButton("Yes") { _, _ ->
                val call2 = client.editTrainerClient(clients[position].id.toInt(),
                    TrainerClient(end_time_ts = Timestamp(System.currentTimeMillis())))
                call2.enqueue(object: Callback<JsonResponse> {
                    override fun onResponse(call: Call<JsonResponse>, response: Response<JsonResponse>) {
                        if(response.code() == 200) {
                            createSnackbar(holder.view.client_card, response.body()?.message!!, Color.RED)
                            refreshClients(clients[position].trainer_id.toInt())
                        }
                    }
                    override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                        createSnackbar(holder.view.client_card, t.message.toString(), Color.RED)
                    }
                })
            }
            builder.setNegativeButton("No") {_, _ ->

            }
            builder.show()
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount() = clients.size

    fun refreshClients(trainer_id: Int) {
        client.getTrainerClients(trainer_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                clients.clear()
                for(r in result.trainer_clients) {
                    if(r.end_time_ts == null && r.response == 1) clients.add(r)
                    else if(r.end_time_ts == null && r.request_response_ts == null) {
                        requests.add(r)
                    }
                }
                notifyDataSetChanged()
            },{ error ->
                //Toast.makeText(context, "Refresh error: ${error.message}", Toast.LENGTH_LONG).show()
            })
    }
}