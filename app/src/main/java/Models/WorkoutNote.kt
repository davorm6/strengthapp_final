package Models

import com.google.gson.annotations.SerializedName

data class WorkoutNote (
    @SerializedName("id")
    val id: Int,
    val user_id: Int,
    val note: String,
    val notification_id: Int? = null,
    val user: User = User(),
    val notification: Notification = Notification()
)

data class WorkoutNoteList (
    val notes: List<WorkoutNote>
)