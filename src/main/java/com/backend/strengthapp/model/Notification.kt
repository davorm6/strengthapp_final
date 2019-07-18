package com.backend.strengthapp.model

import java.io.Serializable
import java.sql.Timestamp
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "notification")
data class Notification (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    val user_id: Long? = null,

    @get: NotNull
    val notification_text: String = "",

    val notification_time_ts: Timestamp = Timestamp(System.currentTimeMillis()),

    val notification_seen_ts: Timestamp? = null,

    @OneToOne(mappedBy = "notification")
    val note: WorkoutNote? = null,

    @OneToOne(mappedBy = "notification")
    val review: TrainerReview? = null
)

@Entity
@Table(name = "workout_notification")
data class WorkoutNotification (
    @EmbeddedId
    @NotNull
    val notificationId: NotificationId = NotificationId(),

    val user_id: Long = 0L,

    @OneToOne(cascade = arrayOf(CascadeType.ALL))
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    var user: User = User(0, "", "", "", ""),

    @OneToOne(cascade = arrayOf(CascadeType.ALL))
    /*@JoinTable
    (
            name="notification",
            joinColumns=arrayOf(JoinColumn(name="id", referencedColumnName = "notification_id"))
    )*/
    @JoinColumn(name = "notification_id", referencedColumnName = "id", insertable = false, updatable = false)
    var notification: Notification = Notification()
)

@Entity
@Table(name = "trainer_client_notification")
data class TrainerClientNotification (
    @EmbeddedId
    @NotNull
    val trainerNotificationId: TrainerNotificationId = TrainerNotificationId(),

    val user_id: Long = 0L,

    @OneToOne(cascade = arrayOf(CascadeType.ALL))
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    var user: User = User(0, "", "", "", ""),

    @OneToOne(cascade = arrayOf(CascadeType.ALL))
    /*@JoinTable
    (
            name="notification",
            joinColumns=arrayOf(JoinColumn(name="id", referencedColumnName = "notification_id"))
    )*/
    @JoinColumn(name = "notification_id", referencedColumnName = "id", insertable = false, updatable = false)
    var notification: Notification = Notification(),

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "trainer_client_id", referencedColumnName = "id", insertable = false, updatable = false)
    var trainer_client: TrainerClient = TrainerClient()
)

data class NotificationList (
        val notifications: List<Any>
)

@Embeddable
class NotificationId(
        @Column(name = "workout_id")
        var workout_id: Long = 0L,

        @Column(name = "notification_id")
        var notification_id: Long = 0L
): Serializable

@Embeddable
class TrainerNotificationId(
        @Column(name = "trainer_client_id")
        var trainer_client_id: Long = 0L,

        @Column(name = "notification_id")
        var notification_id: Long = 0L
): Serializable