package com.backend.strengthapp.model

import net.bytebuddy.implementation.bind.annotation.Default
import java.sql.Timestamp
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
@Table(name = "trainer")
data class Trainer (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0L,

        @get: NotNull
        val user_id: Long = 0L,

        @get: NotNull
        val certificate: String = "",

        val website: String = "",
        val about_me: String = "",

        @Lob
        val profile_photo: ByteArray = byteArrayOf(),

        @OneToOne(cascade = arrayOf(CascadeType.ALL))
        @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
        var user: User = User(0, "", "", "", "")

)

data class TrainerList (
        val trainers: List<Trainer>
)