package com.backend.strengthapp.repository

import com.backend.strengthapp.model.ExerciseSet
import com.backend.strengthapp.model.ExerciseSetId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ExerciseSetRepository: JpaRepository<ExerciseSet, ExerciseSetId> {
    @Query("SELECT CASE WHEN COUNT(w) > 0 THEN 'true' ELSE 'false' END FROM Workout w WHERE w.id = :workoutid")
    fun existsWorkout(workoutid: Long): Boolean

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN 'true' ELSE 'false' END FROM Exercise e WHERE e.id = :exerciseid")
    fun existsExercise(exerciseid: Long): Boolean
}