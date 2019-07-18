package com.backend.strengthapp.model

import javax.persistence.Column
import javax.persistence.Embeddable
import java.io.Serializable
import javax.validation.constraints.NotNull

@Embeddable
class WorkoutExerciseId(
        @Column(name = "workout_id")
        var workout_id: Long = 0L,

        @Column(name = "exercise_id")
        var exercise_id: Long = 0L
): Serializable

@Embeddable
class ExerciseSetId(
        var workoutExerciseId: WorkoutExerciseId = WorkoutExerciseId(),

        @Column(name = "set_number")
        var set_number: Long = 0L
): Serializable