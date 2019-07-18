package com.backend.strengthapp.model

import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "workout_exercise_set")
data class ExerciseSet (
        @EmbeddedId
        @NotNull
        var exerciseSetId: ExerciseSetId = ExerciseSetId(),

        val repetitions: Int = 0,
        val weight: Int = 0

        /*@ManyToOne(fetch = FetchType.EAGER)
        @NotFound(action = NotFoundAction.IGNORE)
        @JoinColumns(JoinColumn(name = "workout_id", referencedColumnName = "workout_id", insertable = false, updatable = false), JoinColumn(name = "exercise_id", referencedColumnName = "exercise_id", insertable = false, updatable = false))
        var workout_exercises: WorkoutExercise = WorkoutExercise()*/
)

data class ExerciseSetList(
        val sets: List<ExerciseSet> = ArrayList()
)