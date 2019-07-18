package com.example.strengthapp

import Models.JsonResponse
import Models.User
import Services.ApiCaller
import Services.SharedPreferenceManager
import Utils.createSnackbar
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_home2.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_trainer_list.*
import kotlinx.android.synthetic.main.app_bar_home2.*
import kotlinx.android.synthetic.main.content_profile.*
import kotlinx.android.synthetic.main.nav_header_home2.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val client by lazy { ApiCaller.create() }

        val navigationView = findViewById<NavigationView>(R.id.nav_view_profile)
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

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout_profile, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout_profile.addDrawerListener(toggle)
        toggle.syncState()

        if(SharedPreferenceManager.getTrainerID(applicationContext) == 0) {
            nav_view_profile.menu.findItem(R.id.trainer_profile).setVisible(false)
        }
        else {
            nav_view_profile.menu.findItem(R.id.trainer_profile).setVisible(true)
        }

        if(!SharedPreferenceManager.getAdmin(applicationContext)) {
            nav_view_profile.menu.findItem(R.id.admin).setVisible(false)
        }
        else {
            nav_view_profile.menu.findItem(R.id.admin).isVisible = true
        }

        nav_view_profile.setNavigationItemSelectedListener(this)

        //View components
        var user: Int = SharedPreferenceManager.getUserID(applicationContext)
        if(intent.hasExtra("id")) {
            user = intent.extras["id"].toString().toInt()
        }
        var user_id = SharedPreferenceManager.getUserID(applicationContext)
        if(user != null) {
            user_id = user.toString().toInt()
        }

        var name: String? = ""
        var surname: String? = ""
        var mail: String? = ""
        var password: String? = ""

        var changes = 0

        val call2 = client.getUser(user_id)
        call2.enqueue(object: Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.code() == 200) {
                    val body = response.body()

                    profile_name.setText(body?.name)
                    name = body?.name
                    profile_surname.setText(body?.surname)
                    surname = body?.surname
                    profile_mail.setText(body?.e_mail)
                    mail = body?.e_mail
                    password = body?.password
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                createSnackbar(username, t.message.toString(), Color.RED)
            }
        })

        save_button.isClickable = false
        //save_button.isEnabled = false

        if(user_id != user) {
            save_button.visibility = View.GONE
            change_pass.visibility = View.GONE
        }
        else {
            profile_name.setOnEditorActionListener { v, actionId, event ->
                if(profile_name.text.toString() != name) {
                    save_button.isClickable = true
                    save_button.isEnabled = true
                    changes += 1
                }
                else {
                    changes -= 1
                    if(changes == 0) {
                        save_button.isClickable = false
                        save_button.isEnabled = false
                    }
                }
                true
            }

            profile_surname.setOnEditorActionListener { v, actionId, event ->
                if(profile_surname.text.toString() != surname) {
                    save_button.isClickable = true
                    save_button.isEnabled = true
                    changes += 1
                }
                else {
                    changes -= 1
                    if(changes == 0) {
                        save_button.isClickable = false
                        save_button.isEnabled = false
                    }
                }
                true
            }
            profile_mail.setOnEditorActionListener { v, actionId, event ->
                if(profile_mail.text.toString() != mail) {
                    save_button.isClickable = true
                    save_button.isEnabled = true
                    changes += 1
                }
                else {
                    changes -= 1
                    if(changes == 0) {
                        save_button.isClickable = false
                        save_button.isEnabled = false
                    }
                }
                true
            }

            save_button.setOnClickListener { v ->
                if(profile_name.text?.isNotEmpty()!! && profile_surname.text?.isNotEmpty()!! && profile_mail.text?.isNotEmpty()!!) {
                    val call3 = client.updateUser(user_id, User(0, profile_mail.text.toString(), password!!, profile_name.text.toString(), profile_surname.text.toString()))

                    call3.enqueue(object: Callback<JsonResponse> {
                        override fun onResponse(call: Call<JsonResponse>, response: Response<JsonResponse>) {
                            if(response.code() == 200) {
                                val builder = AlertDialog.Builder(v.context)
                                builder.setTitle("Successful edit")
                                builder.setMessage("Changes to your profile have been saved.")

                                builder.setPositiveButton("Ok") { _, _ ->
                                    val intent = Intent(applicationContext, ProfileActivity::class.java)
                                    startActivity(intent)
                                }
                                builder.show()
                            }
                        }
                        override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                            createSnackbar(v, t.message.toString(), Color.RED)
                        }

                    })
                }
            }

            change_pass.setOnClickListener { v ->
                val intent = Intent(applicationContext, ChangePasswordActivity::class.java)
                intent.putExtra("id", user_id)
                startActivity(intent)
            }
        }
    }

    override fun onBackPressed() {
        if (drawer_layout_profile.isDrawerOpen(GravityCompat.START)) {
            drawer_layout_profile.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.user_profile, menu)
        if(SharedPreferenceManager.getTrainerCID(applicationContext) > 0) {
            menu.findItem(R.id.action_trainer).setVisible(true)
        }
        else {
            menu.findItem(R.id.action_trainer).setVisible(false)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_trainer -> {
                val intent = Intent(applicationContext, TrainerProfileActivity::class.java)
                intent.putExtra("trainerID", SharedPreferenceManager.getUserTrainerID(applicationContext))
                startActivity(intent)
                return true
            }
            R.id.action_notif -> {
                val intent = Intent(applicationContext, NotificationActivity::class.java)
                startActivity(intent)
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
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

        drawer_layout_profile.closeDrawer(GravityCompat.START)
        return true
    }
}
