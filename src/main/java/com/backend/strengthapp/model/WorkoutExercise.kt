package com.backend.strengthapp.model

import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "workout_exercise")
data class WorkoutExercise (
        @EmbeddedId
        @NotNull
        var workoutExerciseId: WorkoutExerciseId = WorkoutExerciseId(),

        @OneToOne(cascade = arrayOf(CascadeType.ALL))
        @JoinColumn(name = "exercise_id", referencedColumnName = "id", insertable = false, updatable = false)
        var exercise: Exercise = Exercise(),

        val ordinal_number: Int = 0,

        @OneToMany
        @JoinColumns(JoinColumn(name = "workout_id", referencedColumnName = "workout_id", insertable = false, updatable = false),
                JoinColumn(name = "exercise_id", referencedColumnName = "exercise_id", insertable = false, updatable = false))
        var sets: List<ExerciseSet> = emptyList()
)

data class WorkoutExercises (
        val exercises: List<WorkoutExercise>
)