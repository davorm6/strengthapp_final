package Models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName;
import java.sql.Timestamp


data class User(
    @SerializedName("id")
    val id: Int = 0,
    @Expose
    val e_mail: String = "",
    @Expose
    val password: String = "",
    @Expose
    val name: String = "",
    @Expose
    val surname: String = "",
    @Expose
    val mail_confirm: Int = 0,
    val time_of_registration: Timestamp = Timestamp(System.currentTimeMillis()),
    val admin: Int = 0)

data class UserList(
    @SerializedName("users")
    val users: List<User>
)