package com.backend.strengthapp.model

import java.sql.Timestamp
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "workout")
data class Workout (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0L,

        @get: NotNull
        val user_id: Long = 0L,

        val workout_time_ts: Timestamp = Timestamp(System.currentTimeMillis()),

        val user_note_id: Long? = null,
        val trainer_note_id: Long? = null,
        val trainer_client_id: Long? = null,
        val plan: Long = 0L,

        @OneToOne(cascade = [CascadeType.DETACH])
        @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
        var user: User? = User(0, "", "", "", ""),

        @OneToMany
        @JoinColumn(name = "workout_id", referencedColumnName = "id")
        var exercises: List<WorkoutExercise> = emptyList(),

        @OneToOne(cascade = [CascadeType.DETACH])
        @JoinColumn(name = "trainer_client_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = true)
        var trainer_client: TrainerClient? = TrainerClient(),

        @OneToOne
        @JoinColumn(name = "user_note_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = true)
        var user_note: WorkoutNote? = null,

        @OneToOne
        @JoinColumn(name = "trainer_note_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = true)
        var trainer_note: WorkoutNote? = null
)

data class WorkoutList (
        val workouts: List<Workout>
)