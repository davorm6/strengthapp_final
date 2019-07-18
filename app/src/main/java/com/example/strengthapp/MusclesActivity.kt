package com.example.strengthapp

import Models.Muscle
import Models.Type
import Services.ApiCaller
import Utils.createSnackbar
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_types.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MusclesActivity : AppCompatActivity() {

    lateinit var adapter: MuscleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_types)

        val client by lazy { ApiCaller.create() }

        adapter = MuscleAdapter(this.baseContext)

        types.layoutManager = LinearLayoutManager(this)
        types.adapter = adapter

        add.text = "Add muscle"

        add.setOnClickListener { v ->
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Add new muscle")
            builder.setMessage("Input muscle name:")
            val input = EditText(v.context)
            input.inputType = InputType.TYPE_CLASS_TEXT

            builder.setView(input)

            builder.setPositiveButton("Add") {_, _ ->
                if(input.text.length > 0) {
                    client.addMuscle(Muscle(0, input.text.toString())).enqueue(object: Callback<Muscle> {
                        override fun onResponse(call: Call<Muscle>, response: Response<Muscle>) {
                            adapter.refreshMuscles()
                        }
                        override fun onFailure(call: Call<Muscle>, t: Throwable) {
                        }
                    })
                }
                else {
                    createSnackbar(input, "Muscle name must not be empty.", Color.RED)
                }
            }

            builder.setNegativeButton("Cancel") {_, _ -> }
            builder.show()
        }

        backb.setOnClickListener{ v ->
            val intent = Intent(applicationContext, AdminActivity::class.java)
            startActivity(intent)
        }
    }
}
