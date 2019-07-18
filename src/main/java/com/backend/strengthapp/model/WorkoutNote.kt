package com.backend.strengthapp.model

import net.bytebuddy.implementation.bind.annotation.Default
import org.hibernate.annotations.WhereJoinTable
import java.sql.Timestamp
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
@Table(name = "workout_note")
data class WorkoutNote (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0L,

        val user_id: Long = 0L,
        val note: String = "",
        val notification_id: Long? = null,

        @OneToOne(cascade = arrayOf(CascadeType.ALL))
        @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
        var user: User = User(0, "", "", "", ""),

        @OneToOne(cascade = arrayOf(CascadeType.ALL))
        @JoinColumn(name = "notification_id", referencedColumnName = "id", insertable = false, updatable = false)
        var notification: Notification = Notification()
)

data class WorkoutNoteList (
        val notes: List<WorkoutNote>
)