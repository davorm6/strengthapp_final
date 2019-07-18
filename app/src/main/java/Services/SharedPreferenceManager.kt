package Services

import Models.User
import Utils.*
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object SharedPreferenceManager {

    internal fun getPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    /**
     * Set the Login Status
     * @param context
     * @param loggedIn
     */
    fun setLoggedIn(context: Context, loggedIn: Boolean) {
        val editor = getPreferences(context).edit()
        editor.putBoolean(LOGGED_IN_PREF, loggedIn)
        editor.putInt(USER_ID_PREF, 0)
        editor.putInt(TRAINER_ID_PREF, 0)
        editor.apply()
    }

    /**
     * Get the Login Status
     * @param context
     * @return boolean: login status
     */
    fun getLoggedStatus(context: Context): Boolean {
        return getPreferences(context).getBoolean(LOGGED_IN_PREF, false)
    }

    fun setUserID(context: Context, id: Int) {
        val editor = getPreferences(context).edit()
        editor.putInt(USER_ID_PREF, id)
        editor.apply()
    }

    fun getUserID(context: Context): Int {
        if(getLoggedStatus(context)) {
            return getPreferences(context).getInt(USER_ID_PREF, 0)
        }
        return 0
    }

    fun setCurrentWorkout(context: Context, id: Int) {
        val editor = getPreferences(context).edit()
        editor.putInt(CURRENT_WORKOUT_ID_PREF, id)
        editor.apply()
    }

    fun getCurrentWorkout(context: Context): Int {
        return getPreferences(context).getInt(CURRENT_WORKOUT_ID_PREF, 0);
    }

    fun setTrainerID(context: Context, id: Int) {
        val editor = getPreferences(context).edit()
        editor.putInt(TRAINER_ID_PREF, id)
        editor.apply()
    }

    fun getTrainerID(context: Context): Int {
        if(getLoggedStatus(context)) {
            return getPreferences(context).getInt(TRAINER_ID_PREF, 0)
        }
        return 0
    }

    fun setAdmin(context: Context, value: Boolean) {
        val editor = getPreferences(context).edit()
        editor.putBoolean(IS_ADMIN, value)
        editor.apply()
    }

    fun getAdmin(context: Context): Boolean {
        if(getLoggedStatus(context)) {
            val client = ApiCaller.create()
            client.getUser(getPreferences(context).getInt(USER_ID_PREF, 0)).enqueue(object: Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if(response.code() == 200) {
                        if(response.body()?.admin!! > 0) {
                            setAdmin(context, true)
                        }
                        else {
                            setAdmin(context, false)
                        }
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                }
            })
            return getPreferences(context).getBoolean(IS_ADMIN, false)
        }
        return false
    }

    fun setTrainerCID(context: Context, id: Int) {
        val editor = getPreferences(context).edit()
        editor.putInt(TRAINER_CLIENT_ID, id)
        editor.apply()
    }

    fun getTrainerCID(context: Context): Int {
        if(getLoggedStatus(context)) {
            return getPreferences(context).getInt(TRAINER_CLIENT_ID, 0)
        }
        return 0
    }

    fun setUserTrainerID(context: Context, id: Int) {
        val editor = getPreferences(context).edit()
        editor.putInt(TRAINER_CLIENT_TRAINER_ID, id)
        editor.apply()
    }

    fun getUserTrainerID(context: Context): Int {
        if(getLoggedStatus(context)) {
            return getPreferences(context).getInt(TRAINER_CLIENT_TRAINER_ID, 0)
        }
        return 0
    }
}