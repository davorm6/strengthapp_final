package com.backend.strengthapp.repository

import com.backend.strengthapp.model.Trainer
import com.backend.strengthapp.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TrainerRepository : JpaRepository<Trainer, Long> {
    @Query("FROM User WHERE id = :user_id")
    fun findUser(user_id: Long): User

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN 'true' ELSE 'false' END FROM User u WHERE u.id = :user_id")
    fun existsUser(user_id: Long): Boolean

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN 'true' ELSE 'false' END FROM Trainer t where t.user_id = :user_id")
    fun isUserTrainer(user_id: Long): Boolean

    @Query("FROM Trainer WHERE user_id = :user_id")
    fun findTrainerFromUser(user_id: Long): Trainer
}