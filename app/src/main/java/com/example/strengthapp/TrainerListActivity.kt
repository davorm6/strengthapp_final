package com.example.strengthapp

import Models.User
import Services.ApiCaller
import Services.SharedPreferenceManager
import Utils.createSnackbar
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_home2.*
import kotlinx.android.synthetic.main.activity_trainer_list.*
import kotlinx.android.synthetic.main.app_bar_home2.*
import kotlinx.android.synthetic.main.content_trainer.*
import kotlinx.android.synthetic.main.nav_header_home2.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrainerListActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var adapter: TrainerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trainer_list)

        val client by lazy { ApiCaller.create() }

        val navigationView = findViewById<NavigationView>(R.id.nav_view_trainerlist)
        val headerView = navigationView.getHeaderView(0)

        val username = headerView.findViewById<TextView>(R.id.username)
        val usermail = headerView.findViewById<TextView>(R.id.usermail)

        val call = client.getUser(SharedPreferenceManager.getUserID(applicationContext))

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

        setSupportActionBar(toolbar)

        /*fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout_trainerlist, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout_trainerlist.addDrawerListener(toggle)
        toggle.syncState()

        if(SharedPreferenceManager.getTrainerID(applicationContext) == 0) {
            nav_view_trainerlist.menu.findItem(R.id.trainer_profile).setVisible(false)
        }
        else {
            nav_view_trainerlist.menu.findItem(R.id.trainer_profile).setVisible(true)
        }

        if(!SharedPreferenceManager.getAdmin(applicationContext)) {
            nav_view_trainerlist.menu.findItem(R.id.admin).setVisible(false)
        }
        else {
            nav_view_trainerlist.menu.findItem(R.id.admin).isVisible = true
        }

        nav_view_trainerlist.setNavigationItemSelectedListener(this)

        adapter = TrainerAdapter(this.baseContext)

        rv_list_item.layoutManager = LinearLayoutManager(this)
        rv_list_item.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home_activity2, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.refresh -> {
                adapter.refreshTrainers()
                Toast.makeText(this.baseContext, "Refreshed", Toast.LENGTH_LONG).show()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
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

        drawer_layout_trainerlist.closeDrawer(GravityCompat.START)
        return true
    }
}
