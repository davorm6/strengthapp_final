package com.backend.strengthapp.controller

import com.backend.strengthapp.model.*
import com.backend.strengthapp.repository.NotificationRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api")
class NotificationController (private val notificationRepository: NotificationRepository) {
    @GetMapping("/notifications")
    fun getAllNotifications(): ResponseEntity<Any> {
        val notifications: List<Any> = notificationRepository.getAllWorkoutNotification() + notificationRepository.getAllTrainerNotification() + notificationRepository.findAll()
        return ResponseEntity.ok().body(notifications)
    }

    @GetMapping("/notifications/user/{id}")
    fun getAllUserNotifications(@PathVariable(name = "id") user_id: Long): ResponseEntity<Any> {
        var items = notificationRepository.getAllWorkoutNotification()
        var notifications: MutableList<Any> = arrayListOf()
        for(i in items) {
            if(i.user_id == user_id) {
                notifications.add(i)
            }
        }
        var items2 = notificationRepository.getAllTrainerNotification()
        for(i in items2) {
            if(i.user_id == user_id) {
                notifications.add(i)
            }
        }
        var items3 = notificationRepository.findAll()
        for(i in items3) {
            if(i.user_id == user_id) {
                notifications.add(i)
            }
        }
        return ResponseEntity.ok().body(NotificationList(notifications))
    }

    @PostMapping("/notifications")
    fun addNewNotification(@Valid @RequestBody notification: Notification) : ResponseEntity<Any> {
        if(notification.user_id != null && !notificationRepository.existsUser(notification.user_id)) {
            return ResponseEntity.ok(JsonResponse("User with the specified ID does not exist."))
        }
        return ResponseEntity.ok(notificationRepository.save(notification))
    }

    @PutMapping("/notifications/{id}")
    fun editNotification(@PathVariable(name = "id") notificationID: Long,  @Valid @RequestBody notification: Notification) : ResponseEntity<Notification> {
        return notificationRepository.findById(notificationID).map { notif ->
            val updated = notif.copy(notification_seen_ts = notification.notification_seen_ts)
            ResponseEntity.ok().body(notificationRepository.save(updated))
        }.orElse(ResponseEntity.notFound().build())
    }
}