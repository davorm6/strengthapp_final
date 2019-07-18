package com.backend.strengthapp.repository

import com.backend.strengthapp.model.WorkoutNote
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface NoteRepository: JpaRepository<WorkoutNote, Long> {
    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN 'true' ELSE 'false' END FROM Notification n WHERE n.id = :id")
    fun existsNotification(id: Long): Boolean

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN 'true' ELSE 'false' END FROM User u WHERE u.id = :user_id")
    fun existsUser(user_id: Long): Boolean
}