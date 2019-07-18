package com.example.strengthapp

import Models.*
import Services.ApiCaller
import Services.SharedPreferenceManager
import Utils.bytesToBitmap
import Utils.createSnackbar
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.recycler_view_item.view.*
import io.reactivex.android.schedulers.AndroidSchedulers;
import kotlinx.android.synthetic.main.content_trainer_profile.*
import kotlinx.android.synthetic.main.recycler_view_notif.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Timestamp

class NotificationAdapter(val context: Context) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    val client by lazy { ApiCaller.create() }
    var notif: ArrayList<NotificationComb> = ArrayList()

    init { refreshNotifications(0) }

    class NotificationViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): NotificationViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_notif, parent, false)

        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.view.text.text = notif[position].notification.notification_text
        holder.view.date.text = notif[position].notification.notification_time_ts.toString()
        holder.view.notif_card.setOnClickListener { v ->
            if(notif[position].notificationId.workout_id > 0) {
                val intent = Intent(context, WorkoutActivity::class.java)
                intent.putExtra("workoutID", notif[position].notificationId.workout_id)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                ContextCompat.startActivity(context, intent, null)
            }
            else if(notif[position].trainerNotificationId.trainer_client_id > 0) {
                if(notif[position].trainer_client.response == 0) {
                    val builder = AlertDialog.Builder(v.rootView.context)
                    builder.setTitle("Request response")
                    builder.setMessage("Respond to trainer client request:")

                    builder.setPositiveButton("Accept") { _, _ ->
                        val call2 = client.editTrainerClient(
                            notif[position].trainer_client.id.toInt(),
                            TrainerClient(request_response_ts = Timestamp(System.currentTimeMillis()),
                                response = 1))
                        call2.enqueue(object: Callback<JsonResponse> {
                            override fun onResponse(call: Call<JsonResponse>, response: Response<JsonResponse>) {
                                if(response.code() == 200) {
                                    createSnackbar(v, "You have accepted the request", Color.GREEN)
                                    SharedPreferenceManager.setTrainerCID(context, notif[position].trainer_client.id.toInt())
                                    SharedPreferenceManager.setUserTrainerID(context, notif[position].trainer_client.trainer.id)
                                    refreshNotifications(SharedPreferenceManager.getUserID(context))
                                }
                            }
                            override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                                createSnackbar(v, t.message.toString(), Color.RED)
                            }
                        })
                    }
                    builder.setNegativeButton("Deny") {_, _ ->
                        val call2 = client.editTrainerClient(
                            notif[position].trainer_client.id.toInt(),
                            TrainerClient(request_response_ts = Timestamp(System.currentTimeMillis()),
                                response = 2))
                        call2.enqueue(object: Callback<JsonResponse> {
                            override fun onResponse(call: Call<JsonResponse>, response: Response<JsonResponse>) {
                                if(response.code() == 200) {
                                    createSnackbar(v, "You have denied the request", Color.RED)
                                    refreshNotifications(SharedPreferenceManager.getUserID(context))
                                }
                            }
                            override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                                createSnackbar(v, t.message.toString(), Color.RED)
                            }
                        })
                    }
                    builder.show()
                }
                else {
                    val builder = AlertDialog.Builder(v.rootView.context)
                    builder.setTitle("Request response")
                    builder.setMessage("You already responded to this request.")
                    builder.show()
                }
            }
            else {
                if(notif[position].review != null) {
                    val intent = Intent(context, ReviewViewActivity::class.java)
                    intent.putExtra("reviewID", notif[position].review?.id)
                    intent.putExtra("trainerID", notif[position].review?.trainerClient?.trainer_id)
                    intent.putExtra("userID", notif[position].review?.trainerClient?.trainer?.user_id)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    ContextCompat.startActivity(context, intent, null)
                }
            }
        }
    }

    override fun getItemCount() = notif.size

    fun refreshNotifications(user_id: Int) {
        client.getUserNotifications(user_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                for(r in result.notifications) {
                    notif.add(r)
                    if(r.notification.notification_seen_ts == null) {
                        client.editNotification(r.notification.id.toInt(), Notification(notification_seen_ts = Timestamp(System.currentTimeMillis()))).enqueue(object: Callback<Notification> {
                            override fun onResponse(call: Call<Notification>, response: Response<Notification>) {
                            }

                            override fun onFailure(call: Call<Notification>, t: Throwable) {
                            }
                        })
                    }
                }

                notif.sortBy { it.notification.notification_time_ts }

                notif.reverse()

                notifyDataSetChanged()
            },{ error ->
                Toast.makeText(context, "Refresh error: ${error.message}", Toast.LENGTH_LONG).show()
            })
    }
}