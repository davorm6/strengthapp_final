package Models

import com.google.gson.annotations.SerializedName

data class WorkoutExerciseId (
    val workout_id: Int,
    val exercise_id: Int
)

data class WorkoutExercise (
    val workoutExerciseId: WorkoutExerciseId,
    val exercise: Exercise,
    val ordinal_number: Int,
    val sets: List<ExerciseSet>
)

data class WorkoutExerciseList (
    @SerializedName("exercises")
    val exercises: List<WorkoutExercise>
)