package com.example.strengthapp

import Models.*
import Services.ApiCaller
import Services.SharedPreferenceManager
import Utils.bitmapToBytes
import Utils.bytesToBitmap
import Utils.createSnackbar
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toBitmap
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_trainer_profile.*
import kotlinx.android.synthetic.main.app_bar_calendar.*
import kotlinx.android.synthetic.main.content_trainer_profile.*
import kotlinx.android.synthetic.main.nav_header_home2.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Timestamp


class TrainerProfileActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var trainer = 0
    var trainer_user = 0
    var trainer_ident = 0

    var RESULT_LOAD_IMAGE = 1

    val apiclient by lazy {ApiCaller.create()}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trainer_profile)
        setSupportActionBar(toolbar)

        val client by lazy { ApiCaller.create() }

        val navigationView = findViewById<NavigationView>(R.id.nav_view_trainer_profile)
        val headerView = navigationView.getHeaderView(0)

        val username = headerView.findViewById<TextView>(R.id.username)
        val usermail = headerView.findViewById<TextView>(R.id.usermail)


        val trainer_id = intent.getIntExtra("userID", SharedPreferenceManager.getUserID(applicationContext))
        trainer_user = trainer_id
        trainer = intent.getIntExtra("trainerID", SharedPreferenceManager.getTrainerID(applicationContext))

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



        val call2 = client.getUserTrainer(trainer_id)
        call2.enqueue(object: Callback<Trainer> {
            override fun onResponse(call: Call<Trainer>, response: Response<Trainer>) {
                if(response.code() == 200) {
                    val body = response.body()


                    trainer_ident = body?.id!!

                    trainer_name.text = "Trainer: %s %s".format(body?.user?.name, body?.user?.surname)
                    trainer_web.setText(body?.website)
                    trainer_cert.setText(body?.certificate)
                    trainer_about.setText(body?.about_me)
                    if(body?.profile_photo != null && body?.profile_photo.isNotEmpty()) {
                        trainer_avatar.setImageBitmap(bytesToBitmap(body?.profile_photo!!))
                    }
                }
            }

            override fun onFailure(call: Call<Trainer>, t: Throwable) {
                createSnackbar(username, t.message.toString(), Color.RED)
            }
        })

        val call_t = client.getTrainer(trainer)
        call_t.enqueue(object: Callback<Trainer> {
            override fun onResponse(call: Call<Trainer>, response: Response<Trainer>) {
                if(response.code() == 200) {
                    val body = response.body()


                    trainer_ident = body?.id!!

                    trainer_name.text = "Trainer: %s %s".format(body?.user?.name, body?.user?.surname)
                    trainer_web.setText(body?.website)
                    trainer_cert.setText(body?.certificate)
                    trainer_about.setText(body?.about_me)
                    if(body?.profile_photo != null && body?.profile_photo.isNotEmpty()) {
                        trainer_avatar.setImageBitmap(bytesToBitmap(body?.profile_photo!!))
                    }
                }
            }
            override fun onFailure(call: Call<Trainer>, t: Throwable) {
                createSnackbar(username, t.message.toString(), Color.RED)
            }
        })


        if(trainer == SharedPreferenceManager.getTrainerID(applicationContext)) {
            trainer_web.isClickable = true
            trainer_web.isFocusable = true
            trainer_cert.isClickable = true
            trainer_cert.isFocusable = true
            trainer_about.isClickable = true
            trainer_about.isFocusable = true
            trainer_avatar.isClickable = true
            trainer_avatar.isFocusable = true
            savebutton.visibility = View.VISIBLE

            trainer_avatar.setOnClickListener { v ->
                val cameraintent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(cameraintent, RESULT_LOAD_IMAGE)
            }
        }
        else {
            trainer_web.isClickable = false
            trainer_web.isFocusable = false
            trainer_cert.isClickable = false
            trainer_cert.isFocusable = false
            trainer_about.isClickable = false
            trainer_about.isFocusable = false
            trainer_avatar.isClickable = false
            trainer_avatar.isFocusable = false
            savebutton.visibility = View.GONE
        }


        savebutton.setOnClickListener { v ->
            if(trainer_cert.text != null) {
                if(trainer_cert.text!!.isNotEmpty()) {
                    val call3 = client.updateTrainer(trainer, Trainer(0, 0, trainer_cert.text.toString(), trainer_web.text.toString(),
                            trainer_about.text.toString(), bitmapToBytes(trainer_avatar.drawable.toBitmap())))
                    call3.enqueue(object: Callback<JsonResponse> {
                        override fun onResponse(call: Call<JsonResponse>, response: Response<JsonResponse>) {
                            if(response.code() == 200) {
                                val builder = AlertDialog.Builder(v.context)
                                builder.setTitle("Successful edit")
                                builder.setMessage("Changes to your profile have been saved.")

                                builder.setPositiveButton("Ok") { _, _ ->
                                    val intent = Intent(applicationContext, TrainerProfileActivity::class.java)
                                    intent.putExtra("userID", SharedPreferenceManager.getUserID(applicationContext))
                                    intent.putExtra("trainerID", SharedPreferenceManager.getTrainerID(applicationContext))
                                    startActivity(intent)
                                }
                                builder.show()
                            }
                            else {
                                createSnackbar(v, response.message(), Color.RED)
                            }
                        }
                        override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                            createSnackbar(v, t.message.toString(), Color.RED)
                        }
                    })
                }
            }
        }


        val toggle = ActionBarDrawerToggle(
            this, drawer_layout_trainer_profile, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout_trainer_profile.addDrawerListener(toggle)
        toggle.syncState()

        if(SharedPreferenceManager.getTrainerID(applicationContext) == 0) {
            nav_view_trainer_profile.menu.findItem(R.id.trainer_profile).setVisible(false)
        }
        else {
            nav_view_trainer_profile.menu.findItem(R.id.trainer_profile).setVisible(true)
        }

        if(!SharedPreferenceManager.getAdmin(applicationContext)) {
            nav_view_trainer_profile.menu.findItem(R.id.admin).setVisible(false)
        }
        else {
            nav_view_trainer_profile.menu.findItem(R.id.admin).isVisible = true
        }

        nav_view_trainer_profile.setNavigationItemSelectedListener(this)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            val selectedImage = data.data
            var filePathColumn: Array<String> =  arrayOf(MediaStore.Images.Media.DATA)

            /*var cursor: Cursor = contentResolver.query(selectedImage, filePathColumn, null, null, null)
            cursor.moveToFirst()
            var columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
            var picturePath: String = cursor.getString(columnIndex)
            cursor.close()*/

            val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(selectedImage!!))
            trainer_avatar.setImageBitmap(bitmap)
            //pictureFlag = 1

            /*var selectedImage: Uri = data.getData()
            var filePathColumn: Array<String> =  arrayOf(MediaStore.Images.Media.DATA)
            var cursor: Cursor = contentResolver.query(selectedImage, filePathColumn, null, null, null)
            cursor.moveToFirst()
            var columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
            var picturePath: String = cursor.getString(columnIndex)
            cursor.close()

            trainer_avatar.setImageBitmap(BitmapFactory.decodeFile(picturePath))*/
        }
    }

    override fun onBackPressed() {
        if (drawer_layout_trainer_profile.isDrawerOpen(GravityCompat.START)) {
            drawer_layout_trainer_profile.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.trainer_profile, menu)
        if(trainer == SharedPreferenceManager.getTrainerID(applicationContext)) {
            menu.findItem(R.id.action_clients).title = "Clients"
            menu.findItem(R.id.action_clients).setVisible(true)
        }
        else if(SharedPreferenceManager.getUserTrainerID(applicationContext) != 0) {
            if (trainer == SharedPreferenceManager.getUserTrainerID(applicationContext) || trainer_ident == SharedPreferenceManager.getUserTrainerID(
                    applicationContext
                )
            ) {
                menu.findItem(R.id.action_clients).title = "Remove trainer"
            }
            else {
                menu.findItem(R.id.action_clients).title = "Add trainer"
            }
        }
        else {
            //menu.findItem(R.id.action_clients).setVisible(false)
            menu.findItem(R.id.action_clients).title = "Add trainer"
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_clients -> {
                if(trainer == SharedPreferenceManager.getTrainerID(applicationContext)) {
                    val intent = Intent(applicationContext, ClientListActivity::class.java)
                    startActivity(intent)
                }
                else {
                    if(trainer == SharedPreferenceManager.getUserTrainerID(applicationContext) || trainer_ident == SharedPreferenceManager.getUserTrainerID(applicationContext)) {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Remove trainer")
                        builder.setMessage("Are you sure you want to remove your trainer?")

                        builder.setPositiveButton("Yes") { _, _ ->
                            val call2 = apiclient.editTrainerClient(SharedPreferenceManager.getTrainerCID(applicationContext),
                                TrainerClient(end_time_ts = Timestamp(System.currentTimeMillis())))
                            call2.enqueue(object: Callback<JsonResponse> {
                                override fun onResponse(call: Call<JsonResponse>, response: Response<JsonResponse>) {
                                    if(response.code() == 200) {
                                        createSnackbar(savebutton, response.body()?.message!!, Color.RED)
                                        SharedPreferenceManager.setUserTrainerID(applicationContext, 0)
                                        SharedPreferenceManager.setTrainerCID(applicationContext, 0)
                                        val intent = Intent(applicationContext, TrainerProfileActivity::class.java)
                                        intent.putExtra("trainerID", trainer)
                                        startActivity(intent)
                                    }
                                }
                                override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                                    createSnackbar(savebutton, t.message.toString(), Color.RED)
                                }
                            })
                        }
                        builder.setNegativeButton("No") {_, _ ->

                        }
                        builder.show()
                    }
                    else {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Send request")
                        builder.setMessage("Are you sure you want to add this trainer?")

                        builder.setPositiveButton("Yes") { _, _ ->
                            val call2 = apiclient.addTrainerClient(TrainerClient(trainer_id = trainer_ident.toLong(),
                                client_id = SharedPreferenceManager.getUserID(applicationContext).toLong(), sent_by = 1))
                            call2.enqueue(object: Callback<TrainerClient> {
                                override fun onResponse(call: Call<TrainerClient>, response: Response<TrainerClient>) {
                                    if(response.code() == 200) {
                                        if(response.body()?.id!! > 0) createSnackbar(savebutton, "Request successfully sent.", Color.GREEN)
                                        else {
                                            if(SharedPreferenceManager.getTrainerCID(applicationContext) > 0) {
                                                createSnackbar(savebutton, "You already have a trainer.", Color.RED)
                                            }
                                        }
                                    }
                                }
                                override fun onFailure(call: Call<TrainerClient>, t: Throwable) {
                                    createSnackbar(savebutton, t.message.toString(), Color.RED)
                                }
                            })
                        }
                        builder.setNegativeButton("No") {_, _ ->

                        }
                        builder.show()
                    }
                }
                return true
            }
            R.id.action_review -> {
                val intent = Intent(this, ReviewListActivity::class.java)
                intent.putExtra("trainerID", trainer)
                intent.putExtra("userID", trainer_user)
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

        drawer_layout_trainer_profile.closeDrawer(GravityCompat.START)
        return true
    }
}
