package Models

import com.google.gson.annotations.SerializedName

data class ExerciseSetId (
    val set_number: Int,
    val workoutExerciseId: WorkoutExerciseId
)

data class ExerciseSet (
    val exerciseSetId: ExerciseSetId,
    val repetitions: Int,
    val weight: Int
)

data class ExerciseSetList (
    @SerializedName("sets")
    val sets: List<ExerciseSet>
)