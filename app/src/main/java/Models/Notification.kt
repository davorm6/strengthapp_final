package Models

import java.io.Serializable
import java.sql.Timestamp

data class Notification (
    val id: Long = 0L,

    val user_id: Long? = null,

    val notification_text: String = "",

    val notification_time_ts: Timestamp = Timestamp(System.currentTimeMillis()),

    val notification_seen_ts: Timestamp? = null,

    val note: WorkoutNote? = null,

    val review: TrainerReview? = null
)

data class WorkoutNotification (
    val notificationId: NotificationId = NotificationId(),

    val user_id: Long = 0L,

    var user: User = User(),

    var notification: Notification = Notification()
)

data class TrainerClientNotification (
    val trainerNotificationId: TrainerNotificationId = TrainerNotificationId(),

    val user_id: Long = 0L,

    var user: User = User(),

    var notification: Notification = Notification(),

    var trainer_client: TrainerClient = TrainerClient()
)

data class NotificationComb (
    val notificationId: NotificationId = NotificationId(),

    val user_id: Long = 0L,

    var user: User = User(),

    var notification: Notification = Notification(),

    val trainerNotificationId: TrainerNotificationId = TrainerNotificationId(),

    var trainer_client: TrainerClient = TrainerClient(),

    val note: WorkoutNote? = null,

    val review: TrainerReview? = null
)

data class NotificationList (
    val notifications: List<NotificationComb>
)

class NotificationId(
    var workout_id: Long = 0L,

    var notification_id: Long = 0L
)

class TrainerNotificationId(
    var trainer_client_id: Long = 0L,

    var notification_id: Long = 0L
)