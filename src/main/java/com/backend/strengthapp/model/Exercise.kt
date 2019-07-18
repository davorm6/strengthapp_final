package com.backend.strengthapp.model

import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
@Table(name = "exercise")
data class Exercise (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0L,

        @get: NotNull
        val type_id: Long = 0L,

        @get: NotBlank
        val name: String = "",
        val information: String = "",
        val instructions: String = "",

        val request: Int = 0,

        @OneToOne(cascade = arrayOf(CascadeType.ALL))
        @JoinColumn(name = "type_id", referencedColumnName = "id", insertable = false, updatable = false)
        var type: Type = Type(),

        @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
        @JoinTable
        (
                name="exercise_for_muscle",
                joinColumns=arrayOf(JoinColumn(name="exercise_id", referencedColumnName = "id")),
                inverseJoinColumns=arrayOf(JoinColumn(name="muscle_id", referencedColumnName="id"))
        )
// While Update this will also insert collection row another insert
        var muscles: List<Muscle> = emptyList<Muscle>()
)

data class ExerciseList (
        val exercises: List<Exercise> = ArrayList()
)