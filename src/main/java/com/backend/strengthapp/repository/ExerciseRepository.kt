package com.backend.strengthapp.repository

import com.backend.strengthapp.model.Exercise
import com.backend.strengthapp.model.ExerciseList
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional

interface ExerciseRepository: JpaRepository<Exercise, Long> {
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN 'true' ELSE 'false' END FROM Type t WHERE t.id = :type_id")
    fun existsType(type_id: Long): Boolean

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN 'true' ELSE 'false' END FROM Muscle m WHERE m.id = :muscle_id")
    fun existsMuscle(muscle_id: Long): Boolean

    @Query("FROM Exercise e WHERE e.name LIKE %:input% AND e.request = 0")
    fun searchExercise(input: String): List<Exercise>

    @Query("FROM Exercise e WHERE e.request = 1")
    fun getAllRequests(): List<Exercise>

    @Query("FROM Exercise e WHERE e.request = 0")
    fun getAllExercises(): List<Exercise>

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO exercise_for_muscle VALUES (:exercise_id, :muscle_id)", nativeQuery = true)
    fun addExerciseMuscle(exercise_id: Long, muscle_id: Long)

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM exercise_for_muscle WHERE exercise_id = :exercise_id", nativeQuery = true)
    fun deleteExerciseMuscle(exercise_id: Long)

    @Query("SELECT coalesce(max(e.id), 0) FROM Exercise e")
    fun getMaxId(): Long
}