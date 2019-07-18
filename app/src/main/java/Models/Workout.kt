package Models

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class Workout (
    @SerializedName("id")
    val id: Int,
    val user_id: Int,
    val workout_time_ts: Timestamp = Timestamp(System.currentTimeMillis()),
    val user_note_id: Int? = null,
    val trainer_note_id: Int? = null,
    val trainer_client_id: Int? = null,
    val plan: Int = 0,
    val user: User = User(0, "", "", "", ""),
    @SerializedName("exercises")
    val exercises: List<WorkoutExercise> = arrayListOf(),
    val trainer_client: TrainerClient = TrainerClient(),
    val user_note: WorkoutNote? = null,
    val trainer_note: WorkoutNote? = null
)

data class WorkoutList(
    @SerializedName("workouts")
    val workouts: List<Workout>
)