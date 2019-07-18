package Models

import com.google.gson.annotations.Expose
import java.sql.Timestamp

data class JsonResponse(val message: String = "",
                        val id: Int = 0,
                        val e_mail: String = "",
                        val password: String = "",
                        val name: String = "",
                        val surname: String = "",
                        val mail_confirm: Int = 0,
                        val time_of_registration: Timestamp = Timestamp(System.currentTimeMillis()),
                        val admin: Int = 0
)

data class JsonTrainerResponse(
    val message: String = "",
    val id: Int = 0,
    val user_id: Int = 0,
    val certificate: String = "",
    val website: String = "",
    val about_me: String = "",
    val profile_photo: String = "",
    val user: User = User(0, "", "", "", "")
)

data class JsonWorkoutResponse(
    val message: String = "",
    val id: Int = 0,
    val user_id: Int = 0,
    val workout_time_ts: Timestamp = Timestamp(System.currentTimeMillis()),
    val user_note_id: Int = 0,
    val trainer_note_id: Int = 0,
    val trainer_client_id: Int = 0,
    val is_plan: Int = 0,
    val user: User = User(0, "", "", "", "")
)