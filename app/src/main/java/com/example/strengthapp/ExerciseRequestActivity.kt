package com.example.strengthapp

import Models.*
import Services.ApiCaller
import Services.SharedPreferenceManager
import Utils.createSnackbar
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import io.apptik.widget.multiselectspinner.BaseMultiSelectSpinner
import kotlinx.android.synthetic.main.activity_exercise_request.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ExerciseRequestActivity : AppCompatActivity() {

    var values_type: MutableMap<String, Int> = mutableMapOf()
    var values_muscle: MutableMap<String, Int> = mutableMapOf()

    var select_type: MutableList<Int> = arrayListOf()
    var select_muscle: MutableList<Int> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_request)

        backb.setOnClickListener{ v ->
            val intent = Intent(applicationContext, ExerciseListActivity::class.java)
            startActivity(intent)
        }

        val client by lazy { ApiCaller.create() }

        val call = client.getTypes()
        val call2 = client.getMuscles()

        call.enqueue(object: Callback<TypeList> {
            override fun onResponse(call: Call<TypeList>, response: Response<TypeList>) {
                if(response.code() == 200) {
                    var items: MutableList<String> = arrayListOf()
                    for(i in response.body()?.types!!) {
                        items.add(i.name)
                        values_type.put(i.name, i.id)
                    }
                    multiselect_type.setListAdapter(ArrayAdapter<String>(applicationContext, android.R.layout.simple_list_item_multiple_choice, items))
                        .setTitle<BaseMultiSelectSpinner>("Select type")
                        .setAllUncheckedText<BaseMultiSelectSpinner>("Select type")
                        .setAllCheckedText<BaseMultiSelectSpinner>("All types")
                        .setSelectAll<BaseMultiSelectSpinner>(false)
                        .setListener<BaseMultiSelectSpinner>(object: BaseMultiSelectSpinner.MultiSpinnerListener {
                            override fun onItemsSelected(checkedItems: BooleanArray) {
                                var id = 0
                                for(i in checkedItems) {
                                    if(i) {
                                        var value = 0
                                        if(values_type.get(multiselect_type.listAdapter.getItem(id).toString()) != null)
                                            value = values_type.get(multiselect_type.listAdapter.getItem(id).toString())!!
                                        if(value != 0) select_type.add(value)
                                    }
                                    else {
                                        if(select_type.contains(values_type.get(multiselect_type.listAdapter.getItem(id).toString())!!)) {
                                            select_type.remove(values_type.get(multiselect_type.listAdapter.getItem(id).toString())!!)
                                        }
                                    }
                                    id++
                                }
                            }
                        })
                        .setMinSelectedItems<BaseMultiSelectSpinner>(0)
                        .setMaxSelectedItems<BaseMultiSelectSpinner>(1)
                }
            }

            override fun onFailure(call: Call<TypeList>, t: Throwable) {
                createSnackbar(multiselect_type, t.message.toString(), Color.RED)
            }
        })

        call2.enqueue(object: Callback<MuscleList> {
            override fun onResponse(call: Call<MuscleList>, response: Response<MuscleList>) {
                if(response.code() == 200) {
                    var items: MutableList<String> = arrayListOf()
                    for(i in response.body()?.muscles!!) {
                        items.add(i.name)
                        values_muscle.put(i.name, i.id)
                    }
                    multiselect_muscle.setListAdapter(ArrayAdapter<String>(applicationContext, android.R.layout.simple_list_item_multiple_choice, items))
                        .setTitle<BaseMultiSelectSpinner>("Select muscle")
                        .setAllUncheckedText<BaseMultiSelectSpinner>("Select muscle")
                        .setAllCheckedText<BaseMultiSelectSpinner>("All muscles")
                        .setListener<BaseMultiSelectSpinner>(object: BaseMultiSelectSpinner.MultiSpinnerListener {
                            override fun onItemsSelected(checkedItems: BooleanArray) {
                                var id = 0
                                for(i in checkedItems) {
                                    if(i) {
                                        var value = 0
                                        if(values_muscle.get(multiselect_muscle.listAdapter.getItem(id).toString()) != null)
                                            value = values_muscle.get(multiselect_muscle.listAdapter.getItem(id).toString())!!
                                        if(value != 0) select_muscle.add(value)
                                    }
                                    else {
                                        if(select_muscle.contains(values_muscle.get(multiselect_muscle.listAdapter.getItem(id).toString())!!)) {
                                            select_muscle.remove(values_muscle.get(multiselect_muscle.listAdapter.getItem(id).toString())!!)
                                        }
                                    }
                                    id++
                                }
                            }
                        })
                        .setSelectAll<BaseMultiSelectSpinner>(false)
                        .setMinSelectedItems<BaseMultiSelectSpinner>(0)
                }
            }

            override fun onFailure(call: Call<MuscleList>, t: Throwable) {
                createSnackbar(multiselect_type, t.message.toString(), Color.RED)
            }
        })

        send.setOnClickListener { v ->
            if(request_name.text != null) {
                if(request_name.text?.isNotEmpty()!! && select_type.size == 1 && select_muscle.size > 0) {
                    var muscleList: MutableList<Muscle> = arrayListOf()
                    for(m in select_muscle) {
                        muscleList.add(Muscle(m, ""))
                    }
                    val call3 = client.addExercise(Exercise(0, select_type[0], request_name.text.toString(), request_info.text.toString(),
                        request_ins.text.toString(), muscles = muscleList, request = SharedPreferenceManager.getUserID(applicationContext)))

                    call3.enqueue(object: Callback<JsonResponse> {
                        override fun onResponse(call: Call<JsonResponse>, response: Response<JsonResponse>) {
                            if(response.code() == 200) {
                                if(response.body()?.message!!.length > 1) {
                                    createSnackbar(multiselect_muscle, response.body()?.message!!, Color.RED)
                                }
                                else {
                                    val builder = AlertDialog.Builder(v.context)
                                    builder.setTitle("Successful exercise request")
                                    builder.setMessage("Your request has been sent to our administrators.")

                                    builder.setPositiveButton("Ok") { _, _ ->
                                        val intent = Intent(applicationContext, ExerciseListActivity::class.java)
                                        startActivity(intent)
                                    }
                                    builder.show()
                                }
                            }
                        }

                        override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                            createSnackbar(multiselect_muscle, t.message.toString(), Color.RED)
                        }
                    })
                }
                else {
                    createSnackbar(multiselect_muscle, "Name, type and target muscles are required fields.", Color.RED)
                }
            }
        }
    }
}
