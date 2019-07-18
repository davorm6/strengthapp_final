package com.example.strengthapp

import Models.JsonWorkoutResponse
import Models.User
import Models.Workout
import Models.WorkoutList
import Services.ApiCaller
import Services.SharedPreferenceManager
import Utils.createSnackbar
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import io.apptik.widget.multiselectspinner.BaseMultiSelectSpinner
import io.apptik.widget.multiselectspinner.MultiSelectSpinner
import kotlinx.android.synthetic.main.activity_calendar.*
import kotlinx.android.synthetic.main.app_bar_calendar.*
import kotlinx.android.synthetic.main.content_calendar.*
import kotlinx.android.synthetic.main.nav_header_home2.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat

class CalendarActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var adapter: WorkoutAdapter
    var user_value = 0
    var tc_value = 0

    var user_workouts: MutableList<Workout> = mutableListOf()

    var values_workouts: MutableMap<String, Int> = mutableMapOf()
    var values_exercises: MutableMap<String, Int> = mutableMapOf()

    var select_workouts: MutableList<Int> = arrayListOf()
    var select_exercises: MutableList<Int> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        setSupportActionBar(toolbar)

        val client by lazy { ApiCaller.create() }

        val navigationView = findViewById<NavigationView>(R.id.nav_view_calendar)
        val headerView = navigationView.getHeaderView(0)

        val username = headerView.findViewById<TextView>(R.id.username)
        val usermail = headerView.findViewById<TextView>(R.id.usermail)

        val call = client.getUser(SharedPreferenceManager.getUserID(applicationContext))

        var user = intent.getIntExtra("userID", SharedPreferenceManager.getUserID(applicationContext))
        var tcid = intent.getIntExtra("tcID", 0)
        user_value = user
        tc_value = tcid

        call.enqueue(object: Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                createSnackbar(username, t.message.toString(), Color.RED)
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.code() == 200) {
                    val body = response.body()

                    var titula = "User"
                    if(SharedPreferenceManager.getTrainerID(applicationContext) > 0) titula = "Trainer"
                    if(SharedPreferenceManager.getAdmin(applicationContext)) titula = "Admin"

                    username.text = "%s: %s %s".format(titula, body?.name, body?.surname)
                    usermail.text = body?.e_mail
                }
            }
        })

        client.getUserWorkouts(user).enqueue(object: Callback<WorkoutList> {
            override fun onResponse(call: Call<WorkoutList>, response: Response<WorkoutList>) {
                if(response.code() == 200) {
                    for(w in response.body()?.workouts!!) {
                        if(w.plan == 0) user_workouts.add(w)
                    }
                    user_workouts.sortBy { it.workout_time_ts }
                    user_workouts.reverse()
                }
            }

            override fun onFailure(call: Call<WorkoutList>, t: Throwable) {
            }
        })

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout_calendar, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout_calendar.addDrawerListener(toggle)
        toggle.syncState()

        if(SharedPreferenceManager.getTrainerID(applicationContext) == 0) {
            nav_view_calendar.menu.findItem(R.id.trainer_profile).setVisible(false)
        }
        else {
            nav_view_calendar.menu.findItem(R.id.trainer_profile).isVisible = true

        }

        if(!SharedPreferenceManager.getAdmin(applicationContext)) {
            nav_view_calendar.menu.findItem(R.id.admin).setVisible(false)
        }
        else {
            nav_view_calendar.menu.findItem(R.id.admin).isVisible = true
        }

        nav_view_calendar.setNavigationItemSelectedListener(this)

        //calendar part
        val calendar = findViewById<CalendarView>(R.id.calendar)
        val date = calendar.date
        var selected_date: Date? = null

        calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selected_date = SimpleDateFormat("yyyy-MM-dd").parse("%d-%d-%d".format(year, month+1, dayOfMonth))
            val result = adapter.refreshWorkouts(user, dayOfMonth, month+1, year, workout_info_view)
            if (adapter.itemCount > 0) {
                workout_info_view.visibility = View.VISIBLE
            }
            else {
                workout_info_view.visibility = View.GONE
            }
        }

        val button = findViewById<Button>(R.id.add_button)

        button.setOnClickListener { v ->
            var call2:Call<JsonWorkoutResponse>
            if(selected_date == null) {
                call2 = client.addWorkout(
                    Workout(
                        0,
                        user,
                        Timestamp(date),
                        trainer_client_id = if (tcid == 0) null else tcid,
                        plan = if (tcid == 0) 0 else 1
                    )
                )
            }
            else {
                call2 = client.addWorkout(
                    Workout(
                        0,
                        user,
                        Timestamp(selected_date!!.time),
                        trainer_client_id = if (tcid == 0) null else tcid,
                        plan = if (tcid == 0) 0 else 1
                    )
                )
            }
            call2.enqueue(object: Callback<JsonWorkoutResponse> {
                override fun onResponse(call: Call<JsonWorkoutResponse>, response: Response<JsonWorkoutResponse>) {
                    if(response.code() == 200) {
                        val response_body = response.body()

                        if (response_body?.message?.length!! > 1) {
                            createSnackbar(v, response_body.message.toString(), Color.RED)
                        } else {
                            val intent = Intent(applicationContext, WorkoutActivity::class.java)
                            intent.putExtra("workoutID", response_body.id)
                            startActivity(intent)
                        }
                    }
                }

                override fun onFailure(call: Call<JsonWorkoutResponse>, t: Throwable) {
                    createSnackbar(v, t.message.toString(), Color.RED)
                }
            })
        }

        adapter = WorkoutAdapter(this.baseContext)

        workout_info_view.layoutManager = LinearLayoutManager(this)
        workout_info_view.adapter = adapter

        val day = SimpleDateFormat("dd").format(date).toInt()
        val month = SimpleDateFormat("MM").format(date).toInt()
        val year = SimpleDateFormat("yyyy").format(date).toInt()

        adapter.refreshWorkouts(user, day, month, year, workout_info_view)

        if (adapter.itemCount > 0) {
            workout_info_view.visibility = View.VISIBLE
        }
        else {
            workout_info_view.visibility = View.GONE
        }
    }

    override fun onPostResume() {
        val calendar = findViewById<CalendarView>(R.id.calendar)
        val date = calendar.date

        calendar.setDate(date, true, false)

        val day = SimpleDateFormat("dd").format(date).toInt()
        val month = SimpleDateFormat("MM").format(date).toInt()
        val year = SimpleDateFormat("yyyy").format(date).toInt()

        adapter.refreshWorkouts(user_value, day, month, year, workout_info_view)

        if (adapter.itemCount > 0) {
            workout_info_view.visibility = View.VISIBLE
        }
        else {
            workout_info_view.visibility = View.GONE
        }

        super.onPostResume()
    }

    override fun onBackPressed() {
        if (drawer_layout_calendar.isDrawerOpen(GravityCompat.START)) {
            drawer_layout_calendar.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.calendar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.graph -> {
                values_workouts.clear()
                values_exercises.clear()

                select_workouts.clear()
                select_exercises.clear()

                val builder = AlertDialog.Builder(this)
                builder.setTitle("Create progress graph")
                val holder = LinearLayout(this)
                holder.orientation = LinearLayout.VERTICAL

                val select_w = MultiSelectSpinner(this)
                val select_e = MultiSelectSpinner(this)

                var items: MutableList<String> = arrayListOf()
                var items_e: MutableList<String> = arrayListOf()
                for(i in user_workouts) {
                    items.add("Workout #%d".format(i.id))
                    values_workouts.put("Workout #%d".format(i.id), i.id)
                }
                select_w.setListAdapter(ArrayAdapter<String>(applicationContext, android.R.layout.simple_list_item_multiple_choice, items))
                    .setTitle<BaseMultiSelectSpinner>("Select workouts")
                    .setAllUncheckedText<BaseMultiSelectSpinner>("Select workouts")
                    .setAllCheckedText<BaseMultiSelectSpinner>("All workouts selected")
                    .setSelectAll<BaseMultiSelectSpinner>(false)
                    .setListener<BaseMultiSelectSpinner>(object: BaseMultiSelectSpinner.MultiSpinnerListener {
                        override fun onItemsSelected(checkedItems: BooleanArray) {
                            var id = 0
                            for(i in checkedItems) {
                                if(i) {
                                    var value = 0
                                    if(values_workouts.get(select_w.listAdapter.getItem(id).toString()) != null)
                                        value = values_workouts.get(select_w.listAdapter.getItem(id).toString())!!
                                    if(value != 0) select_workouts.add(value)

                                    items_e.clear()
                                    values_exercises.clear()

                                    for(w in user_workouts) {
                                        if(select_workouts.contains(w.id)) {
                                            for(e in w.exercises) {
                                                if (!values_exercises.values.contains(e.exercise.id)) {
                                                    items_e.add(e.exercise.name)
                                                    values_exercises.put(e.exercise.name, e.exercise.id)
                                                }
                                            }
                                        }
                                    }
                                    select_e.setItems(items_e)
                                }
                                else {
                                    if(select_workouts.contains(values_workouts.get(select_w.listAdapter.getItem(id).toString())!!)) {
                                        select_workouts.remove(values_workouts.get(select_w.listAdapter.getItem(id).toString())!!)

                                        items_e.clear()
                                        values_exercises.clear()

                                        for(w in user_workouts) {
                                            if(select_workouts.contains(w.id)) {
                                                for(e in w.exercises) {
                                                    if (!values_exercises.values.contains(e.exercise.id)) {
                                                        items_e.add(e.exercise.name)
                                                        values_exercises.put(e.exercise.name, e.exercise.id)
                                                    }
                                                }
                                            }
                                        }
                                        select_e.setItems(items_e)
                                    }
                                }
                                id++
                            }
                        }
                    })
                    .setMinSelectedItems<BaseMultiSelectSpinner>(0)

                holder.addView(select_w)

                for(i in user_workouts) {
                    //if(select_workouts.contains(i.id)) {
                        for(e in i.exercises) {
                            if (!values_exercises.values.contains(e.exercise.id)) {

                            //if(!items_e.contains(e.exercise.name)) {
                                items_e.add(e.exercise.name)
                                values_exercises.put(e.exercise.name, e.exercise.id)
                                }
                            }
                }
                    //}
                //}

                select_e.setListAdapter(ArrayAdapter<String>(applicationContext, android.R.layout.simple_list_item_multiple_choice, items_e))
                    .setTitle<BaseMultiSelectSpinner>("Select exercises")
                    .setAllUncheckedText<BaseMultiSelectSpinner>("Select exercises")
                    .setAllCheckedText<BaseMultiSelectSpinner>("All exercises selected")
                    .setSelectAll<BaseMultiSelectSpinner>(false)
                    .setListener<BaseMultiSelectSpinner>(object: BaseMultiSelectSpinner.MultiSpinnerListener {
                        override fun onItemsSelected(checkedItems: BooleanArray) {
                            var id = 0
                            for(i in checkedItems) {
                                if(i) {
                                    var value = 0
                                    if(values_exercises.get(select_e.listAdapter.getItem(id).toString()) != null)
                                        value = values_exercises.get(select_e.listAdapter.getItem(id).toString())!!
                                    if(value != 0) select_exercises.add(value)
                                }
                                else {
                                    if(select_exercises.contains(values_exercises.get(select_e.listAdapter.getItem(id).toString())!!)) {
                                        select_exercises.remove(values_exercises.get(select_e.listAdapter.getItem(id).toString())!!)
                                    }
                                }
                                id++
                            }
                        }
                    })
                    .setMinSelectedItems<BaseMultiSelectSpinner>(0)

                holder.addView(select_e)
                builder.setView(holder)

                builder.setPositiveButton("Create") {_, _ ->
                    if(select_workouts.size <= 0 || select_exercises.size <= 0) {
                        builder.setMessage("You must select at least 1 workout and 1 exercise.")
                    }
                    var send_map: HashMap<String, HashMap<Timestamp, Int>> = hashMapOf()
                    for(w in user_workouts) {
                        if (select_workouts.contains(w.id)) {
                            var volume = 0
                            for (e in w.exercises) {
                                if (select_exercises.contains(e.exercise.id)) {
                                    for (s in e.sets) {
                                        volume += s.weight * s.repetitions
                                    }
                                    if (!send_map.keys.contains(e.exercise.name)) {
                                        send_map.put(e.exercise.name, hashMapOf(w.workout_time_ts to volume))
                                    }
                                    else {
                                        send_map[e.exercise.name]?.put(w.workout_time_ts, volume)
                                    }
                                    /*if(send_map.keys.contains(e.exercise.name)) {
                                        if(send_map[e.exercise.name]?.contains(w.workout_time_ts)!!) {
                                            send_map[e.exercise.name]?.set(
                                                w.workout_time_ts,
                                                send_map[e.exercise.name]?.get(w.workout_time_ts)!! + volume
                                            )
                                        }
                                    }
                                    else {
                                        send_map.put(e.exercise.name, hashMapOf(w.workout_time_ts to volume))
                                    }*/
                                }
                            }
                        }
                    }

                    val intent = Intent(this, GraphActivity::class.java)
                    intent.putExtra("data", send_map)
                    intent.putExtra("userID", user_value)
                    intent.putExtra("tcID", tc_value)
                    startActivity(intent)
                }

                builder.setNegativeButton("Cancel") {_, _ -> }
                builder.show()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
        //return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.home -> {
                val intent = Intent(applicationContext, HomeActivity::class.java)
                startActivity(intent)
            }
            R.id.personal -> {
                val intent = Intent(applicationContext, ProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.calendar -> {
                val intent = Intent(applicationContext, CalendarActivity::class.java)
                startActivity(intent)
            }
            R.id.trainers -> {
                val intent = Intent(applicationContext, TrainerListActivity::class.java)
                startActivity(intent)
            }
            R.id.trainer_profile -> {
                val intent = Intent(applicationContext, TrainerProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.admin -> {
                if(SharedPreferenceManager.getAdmin(applicationContext)) {
                    val intent = Intent(applicationContext, AdminActivity::class.java)
                    startActivity(intent)
                }
            }
            R.id.logout -> {
                SharedPreferenceManager.setLoggedIn(applicationContext, false)

                val intent = Intent(applicationContext, MainActivity::class.java)
                createSnackbar(username, "Successfully logged out", Color.RED)
                Handler().postDelayed(Runnable {
                    startActivity(intent)
                }, 2000)
            }
        }

        drawer_layout_calendar.closeDrawer(GravityCompat.START)
        return true
    }
}
