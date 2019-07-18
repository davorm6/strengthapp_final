package com.example.strengthapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_admin.*

class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        types.setOnClickListener { v ->
            val intent = Intent(applicationContext, TypesActivity::class.java)
            startActivity(intent)
        }

        muscles.setOnClickListener { v ->
            val intent = Intent(applicationContext, MusclesActivity::class.java)
            startActivity(intent)
        }

        requests.setOnClickListener { v ->
            val intent = Intent(applicationContext, RequestReviewActivity::class.java)
            startActivity(intent)
        }

        backb.setOnClickListener { v ->
            val intent = Intent(applicationContext, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}
