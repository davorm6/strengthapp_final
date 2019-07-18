package Models

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class TrainerClient (
    @SerializedName("id")
    val id: Long = 0L,

    val trainer_id: Long = 0L,
    val client_id: Long = 0L,
    val request_sent_ts: Timestamp = Timestamp(System.currentTimeMillis()),
    val request_response_ts: Timestamp? = null,
    val end_time_ts: Timestamp? = null,
    val response: Int = 0,
    val sent_by: Int = 0,

    var trainer: Trainer = Trainer(),

    var client: User = User()
)

data class TrainerClientList (
    @SerializedName("trainer_clients")
    val trainer_clients: List<TrainerClient>
)