package com.backend.strengthapp.repository

import com.backend.strengthapp.model.Trainer
import com.backend.strengthapp.model.TrainerClient
import com.backend.strengthapp.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@Repository
interface TrainerClientRepository: JpaRepository<TrainerClient, Long> {
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN 'true' ELSE 'false' END FROM User u WHERE u.id = :user_id")
    fun existsUser(user_id: Long): Boolean

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN 'true' ELSE 'false' END FROM Trainer u WHERE u.id = :user_id")
    fun existsTrainer(user_id: Long): Boolean

    @Query("FROM User WHERE id = :user_id")
    fun findUser(user_id: Long): User

    @Query("FROM Trainer WHERE id = :user_id")
    fun findTrainer(user_id: Long): Trainer

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN 'true' ELSE 'false' END FROM TrainerClient u WHERE u.client_id = :user_id AND u.request_response_ts is NOT NULL AND u.end_time_ts is null AND u.response = 1")
    fun existsUserClient(user_id: Long): Boolean

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN 'true' ELSE 'false' END FROM TrainerClient u WHERE u.trainer_id = :user_id AND u.request_response_ts is NOT NULL AND u.end_time_ts is null  AND u.response = 1")
    fun existsClientTrainer(user_id: Long): Boolean

    @Query("FROM TrainerClient t WHERE t.client_id = :user_id AND t.request_response_ts is NOT NULL AND t.end_time_ts is null AND t.response = 1")
    fun getUserClient(user_id: Long): TrainerClient

    @Query("FROM TrainerClient t WHERE t.client_id = :user_id AND t.request_response_ts IS NOT NULL and t.response = 1")
    fun getUserTrainers(user_id: Long): List<TrainerClient>

    @Query("FROM TrainerClient t WHERE t.trainer_id = :user_id")
    fun getTrainerClient(user_id: Long): List<TrainerClient>

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN 'true' ELSE 'false' END FROM TrainerClient u WHERE u.trainer_id = :trainer_id AND u.client_id = :user_id")
    fun existsTrainerClient(trainer_id: Long, user_id: Long): Boolean

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO notification (notification_text, notification_time_ts, notification_seen_ts) VALUES (:text, :timet, NULL)", nativeQuery = true)
    fun addNotification(text: String, timet: Timestamp)

    @Query("SELECT coalesce(max(n.id), 0) FROM Notification n")
    fun getMaxNId(): Long

    @Query("SELECT coalesce(max(n.id), 0) FROM TrainerClient n")
    fun getMaxTCId(): Long

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO trainer_client_notification (trainer_client_id, notification_id, user_id) VALUES (:tcid, :nid, :uid)", nativeQuery = true)
    fun addTrainerNotification(tcid: Long, nid: Long, uid: Long)
}