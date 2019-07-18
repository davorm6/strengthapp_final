package com.backend.strengthapp.model

import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "muscle")
data class Muscle (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0L,

        @get: NotNull
        val name: String = ""
)

data class MuscleList (
        val muscles: List<Muscle>
)