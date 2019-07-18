package com.backend.strengthapp.model

import java.sql.Timestamp
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "user")
data class User (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @get: NotNull
    val e_mail: String = "",

    @get: NotNull
    val password: String = "",

    @get: NotNull
    val name: String = "",

    @get: NotNull
    val surname: String = "",

    val mail_confirm: Int = 0,

    val time_of_registration: Timestamp = Timestamp(System.currentTimeMillis()),

    val admin: Long = 0L
)

data class UserList (
        val users: List<User>
)