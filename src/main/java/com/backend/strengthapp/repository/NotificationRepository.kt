package com.backend.strengthapp.repository

import com.backend.strengthapp.model.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@Repository
interface NotificationRepository: JpaRepository<Notification, Long> {
    @Query("FROM WorkoutNotification wn")
    fun getAllWorkoutNotification(): List<WorkoutNotification>

    @Query("FROM TrainerClientNotification tn")
    fun getAllTrainerNotification(): List<TrainerClientNotification>

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN 'true' ELSE 'false' END FROM User u WHERE u.id = :user_id")
    fun existsUser(user_id: Long): Boolean
}