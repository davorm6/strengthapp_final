package com.backend.strengthapp.model

import net.bytebuddy.implementation.bind.annotation.Default
import org.hibernate.annotations.WhereJoinTable
import java.sql.Timestamp
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
@Table(name = "trainer_review")
data class TrainerReview (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0L,

        val trainer_client_id: Long = 0L,
        val review_text: String = "",
        val review_add_ts: Timestamp = Timestamp(System.currentTimeMillis()),
        val notification_id: Long = 0L,

        @OneToOne(cascade = arrayOf(CascadeType.ALL))
        @JoinColumn(name = "trainer_client_id", referencedColumnName = "id", insertable = false, updatable = false)
        var trainerClient: TrainerClient = TrainerClient(),

        @OneToOne(cascade = arrayOf(CascadeType.ALL))
        @JoinColumn(name = "notification_id", referencedColumnName = "id", insertable = false, updatable = false)
        var notification: Notification = Notification()
)

data class TrainerReviewList (
        val reviews: List<TrainerReview>
)