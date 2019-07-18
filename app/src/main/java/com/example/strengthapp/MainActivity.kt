package com.example.strengthapp

import Services.SharedPreferenceManager
import Utils.createSnackbar
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(SharedPreferenceManager.getLoggedStatus(applicationContext)) {
            if(SharedPreferenceManager.getUserID(applicationContext) > 0) {
                val intent = Intent(applicationContext, HomeActivity::class.java)
                Handler().postDelayed(Runnable {
                    startActivity(intent)
                }, 2000)
            }
        }

        val reg_button = findViewById(R.id.register_button) as Button
        val log_button = findViewById(R.id.login_button) as Button

        reg_button.setOnClickListener { v ->
            val intent = Intent(v.context, RegisterActivity::class.java)
            startActivity(intent)
        }

        log_button.setOnClickListener { v ->
            val intent = Intent(v.context, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
