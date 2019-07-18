package Models

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class TrainerReview(
    @SerializedName("id")
    val id: Int,
    val trainer_client_id: Int,
    val review_text: String,
    val review_add_ts: Timestamp = Timestamp(System.currentTimeMillis()),
    val notification_id: Int,
    val trainerClient: TrainerClient = TrainerClient(),
    val notification: Notification = Notification()
)

data class TrainerReviewList(
    val reviews: List<TrainerReview>
)