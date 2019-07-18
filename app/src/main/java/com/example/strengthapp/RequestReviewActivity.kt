package com.example.strengthapp

import Services.ApiCaller
import Services.SharedPreferenceManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_request_review.*

class RequestReviewActivity : AppCompatActivity() {

    lateinit var adapter: ExerciseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_review)

        val client by lazy { ApiCaller.create() }

        adapter = ExerciseAdapter(this.baseContext)

        items.layoutManager = LinearLayoutManager(this)
        items.adapter = adapter

        adapter.refreshRequests()

        backb.setOnClickListener { v ->
            val intent = Intent(applicationContext, AdminActivity::class.java)
            startActivity(intent)
        }

        if(!SharedPreferenceManager.getAdmin(applicationContext)) {
            val intent = Intent(applicationContext, HomeActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        adapter.refreshRequests()
    }

    override fun onRestart() {
        super.onRestart()
        adapter.refreshRequests()
    }

    override fun onResume() {
        super.onResume()
        adapter.refreshRequests()
    }
}
