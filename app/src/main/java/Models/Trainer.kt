package Models

import com.google.gson.annotations.SerializedName

data class Trainer(
    val id: Int = 0,
    val user_id: Int = 0,
    val certificate: String = "",
    val website: String = "",
    val about_me: String = "",
    val profile_photo: ByteArray = byteArrayOf(),
    val user: User = User(0, "", "", "", "")
)

data class TrainerList(
    @SerializedName("trainers")
    val trainers: List<Trainer>
)