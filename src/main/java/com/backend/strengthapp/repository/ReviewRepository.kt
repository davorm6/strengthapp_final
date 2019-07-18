package com.backend.strengthapp.repository

import com.backend.strengthapp.model.TrainerClient
import com.backend.strengthapp.model.TrainerReview
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ReviewRepository: JpaRepository<TrainerReview, Long> {
    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN 'true' ELSE 'false' END FROM Notification n WHERE n.id = :id")
    fun existsNotification(id: Long): Boolean

    @Query("SELECT CASE WHEN COUNT(tc) > 0 THEN 'true' ELSE 'false' END FROM TrainerClient tc WHERE tc.id = :id")
    fun existsTrainerClient(id: Long): Boolean

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN 'true' ELSE 'false' END FROM TrainerReview r WHERE r.trainer_client_id = :id")
    fun existsReviewForTrainerClient(id: Long): Boolean
}