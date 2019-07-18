package com.backend.strengthapp.model

import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "type")
data class Type (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0L,

        @get: NotNull
        val name: String = ""
)

data class TypeList (
        val types: List<Type>
)