package com.example.strengthapp

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

class TypesActivity : AppCompatActivity() {

    lateinit var adapter: TypeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_types)

        val client by lazy { ApiCaller.create() }

        adapter = TypeAdapter(this.baseContext)

        types.layoutManager = LinearLayoutManager(this)
        types.adapter = adapter

        add.setOnClickListener { v ->
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Add new type")
            builder.setMessage("Input type name:")
            val input = EditText(v.context)
            input.inputType = InputType.TYPE_CLASS_TEXT

            builder.setView(input)

            builder.setPositiveButton("Add") {_, _ ->
                if(input.text.length > 0) {
                    client.addType(Type(0, input.text.toString())).enqueue(object: Callback<Type> {
                        override fun onResponse(call: Call<Type>, response: Response<Type>) {
                            adapter.refreshTypes()
                        }
                        override fun onFailure(call: Call<Type>, t: Throwable) {
                        }
                    })
                }
                else {
                    createSnackbar(input, "Type name must not be empty.", Color.RED)
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
