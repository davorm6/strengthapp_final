package com.backend.strengthapp.repository

import com.backend.strengthapp.model.Exercise
import com.backend.strengthapp.model.JsonResponse
import com.backend.strengthapp.model.WorkoutExercise
import com.backend.strengthapp.model.WorkoutExerciseId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional

interface WorkoutExerciseRepository: JpaRepository<WorkoutExercise, WorkoutExerciseId> {
    @Query("SELECT CASE WHEN COUNT(w) > 0 THEN 'true' ELSE 'false' END FROM Workout w WHERE w.id = :workoutid")
    fun existsWorkout(workoutid: Long): Boolean

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN 'true' ELSE 'false' END FROM Exercise e WHERE e.id = :exerciseid")
    fun existsExercise(exerciseid: Long): Boolean

    @Query("FROM Exercise WHERE id = :exerciseid")
    fun getExerciseFromId(exerciseid: Long): Exercise

    @Transactional
    @Modifying
    @Query("DELETE FROM ExerciseSet WHERE workout_id = :workoutid AND exercise_id = :exerciseid")
    fun clearExerciseSets(workoutid: Long, exerciseid: Long)

    @Transactional
    @Modifying
    @Query("DELETE FROM WorkoutExercise WHERE workout_id = :workoutid AND exercise_id = :exerciseid")
    fun deleteWorkoutExercise(workoutid: Long, exerciseid: Long)
}