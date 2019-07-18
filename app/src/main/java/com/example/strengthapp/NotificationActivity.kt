package com.example.strengthapp

import Services.SharedPreferenceManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_notification.*

class NotificationActivity : AppCompatActivity() {

    lateinit var adapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        adapter = NotificationAdapter(this.baseContext)

        items.layoutManager = LinearLayoutManager(this)
        items.adapter = adapter

        adapter.refreshNotifications(SharedPreferenceManager.getUserID(applicationContext))

        backb.setOnClickListener{ v ->
            val intent = Intent(applicationContext, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}
