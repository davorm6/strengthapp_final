package com.backend.strengthapp.repository

import com.backend.strengthapp.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    @Query("FROM User WHERE e_mail = :mail")
    fun findUserByMail(@Param("mail") mail: String): User

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN 'true' ELSE 'false' END FROM User u WHERE u.e_mail = :mail")
    fun existsUserByE_mail(mail: String): Boolean

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN 'true' ELSE 'false' END FROM User u WHERE u.e_mail LIKE %:mail%")
    fun existsUserByE_mailName(mail: String): Boolean

    @Query("FROM User WHERE e_mail LIKE :input%")
    fun findUserByMailName(input: String): User
}