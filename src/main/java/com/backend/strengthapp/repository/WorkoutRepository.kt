package com.backend.strengthapp.repository

import com.backend.strengthapp.model.ExerciseSetList
import com.backend.strengthapp.model.User
import com.backend.strengthapp.model.Workout
import com.backend.strengthapp.model.WorkoutExercises
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@Repository
interface WorkoutRepository : JpaRepository<Workout, Long> {
    @Query("FROM Workout WHERE user_id = :user_id")
    fun findWorkoutsByUser_id(user_id: Long): List<Workout>

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN 'true' ELSE 'false' END FROM User u WHERE u.id = :user_id")
    fun existsUser(user_id: Long): Boolean

    @Query("FROM User WHERE id = :user_id")
    fun findUser(user_id: Long): User

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO notification (notification_text, notification_time_ts, notification_seen_ts) VALUES (:text, :timet, NULL)", nativeQuery = true)
    fun addNotification(text: String, timet: Timestamp)

    @Query("SELECT coalesce(max(n.id), 0) FROM Notification n")
    fun getMaxNId(): Long

    @Query("SELECT coalesce(max(n.id), 0) FROM Workout n")
    fun getMaxWId(): Long

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO workout_notification (workout_id, notification_id, user_id) VALUES (:wid, :nid, :uid)", nativeQuery = true)
    fun addWorkoutNotification(wid: Long, nid: Long, uid: Long)

    @Query("FROM WorkoutExercise w WHERE workout_id = :id")
    fun getWorkoutExercises(id: Long): WorkoutExercises

    @Query("FROM ExerciseSet w WHERE workout_id = :id AND exercise_id = :eid")
    fun getWorkoutExerciseSets(id: Long, eid: Long): ExerciseSetList

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO workout_exercise (workout_id, exercise_id, ordinal_number) VALUES (:wid, :eid, :num)", nativeQuery = true)
    fun addWorkoutExercise(wid: Long, eid: Long, num: Int)

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO workout_exercise_set (workout_id, exercise_id, set_number, weight, repetitions) VALUES (:wid, :eid, :snum, 0, 0)", nativeQuery = true)
    fun addExerciseSet(wid: Long, eid: Long, snum: Long)
}