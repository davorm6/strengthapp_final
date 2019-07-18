package com.backend.strengthapp.controller

import com.backend.strengthapp.model.JsonResponse
import com.backend.strengthapp.model.Notification
import com.backend.strengthapp.model.TrainerClient
import com.backend.strengthapp.model.TrainerClientList
import com.backend.strengthapp.repository.TrainerClientRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api")
class TrainerClientController (private val trainerClientRepository: TrainerClientRepository) {
    @GetMapping("/trainer_clients")
    fun getAllClients(): TrainerClientList {
        return TrainerClientList(trainerClientRepository.findAll())
    }

    @GetMapping("/trainer_clients/user/{id}")
    fun getUserClient(@PathVariable(name = "id") user_id: Long): ResponseEntity<TrainerClient> {
        if(trainerClientRepository.existsUser(user_id)) {
            return ResponseEntity.ok().body(trainerClientRepository.getUserClient(user_id))
        }
        return ResponseEntity.notFound().build()
    }

    @GetMapping("/trainer_clients/trainer/{id}")
    fun getTrainerClients(@PathVariable(name = "id") user_id: Long): ResponseEntity<TrainerClientList> {
        if(trainerClientRepository.existsTrainer(user_id)) {
            return ResponseEntity.ok().body(TrainerClientList(trainerClientRepository.getTrainerClient(user_id)))
        }
        return ResponseEntity.notFound().build()
    }

    @GetMapping("/trainer_clients/user_c/{id}")
    fun getUserClients(@PathVariable(name = "id") user_id: Long): ResponseEntity<TrainerClientList> {
        if(trainerClientRepository.existsUser(user_id)) {
            return ResponseEntity.ok().body(TrainerClientList(trainerClientRepository.getUserTrainers(user_id)))
        }
        return ResponseEntity.notFound().build()
    }

    @PostMapping("/trainer_clients")
    fun addTrainerClient(@Valid @RequestBody trainerClient: TrainerClient): ResponseEntity<Any> {
        if(trainerClientRepository.existsUser(trainerClient.client_id)) {
            if(trainerClientRepository.existsTrainer(trainerClient.trainer_id)) {

                if(trainerClientRepository.existsUserClient(trainerClient.client_id)) {
                    return ResponseEntity.ok().body(JsonResponse("This user already has a trainer."))
                }

                trainerClient.trainer = trainerClientRepository.findTrainer(trainerClient.trainer_id)
                trainerClient.client = trainerClientRepository.findUser(trainerClient.client_id)

                val result = trainerClientRepository.save(trainerClient)
                val tcid = trainerClientRepository.getMaxTCId()
                var text: String
                var user: Long
                if(trainerClient.sent_by == 0) {
                    text = "Trainer " + trainerClient.trainer.user.name + " " + trainerClient.trainer.user.surname + " has added you as his client. Please respond to this request."
                    user = trainerClient.client_id
                }
                else {
                    text = "User " + trainerClient.client?.name + " " + trainerClient.client?.surname + " has added you as his trainer. Please respond to this request."
                    user = trainerClient.trainer.user_id
                }
                val n = Notification(notification_text = text)
                trainerClientRepository.addNotification(text, n.notification_time_ts)
                trainerClientRepository.addTrainerNotification(tcid, trainerClientRepository.getMaxNId(), user)
                return ResponseEntity.ok().body(result)
            }
        }
        return ResponseEntity.notFound().build()
    }

    @PutMapping("/trainer_clients/{id}")
    fun editTrainerClient(@PathVariable(value = "id") trainerClientID: Long, @Valid @RequestBody trainerClient: TrainerClient): ResponseEntity<JsonResponse> {
        return trainerClientRepository.findById(trainerClientID).map { trainerC ->
            val updated = trainerC.copy(
                    request_response_ts = if (trainerClient.request_response_ts != trainerC.request_response_ts && trainerClient.request_response_ts != null) trainerClient.request_response_ts else trainerC.request_response_ts
                    , end_time_ts = trainerClient.end_time_ts,
                    response = if (trainerClient.response != trainerC.response && trainerClient.response != 0) trainerClient.response else trainerC.response)
            trainerClientRepository.save(updated)
            var message = ""
            var text = ""
            var text2 = ""
            var user = 0L
            var user2 = 0L
            if(trainerC.response != trainerClient.response) {
                if(trainerClient.response == 1) {
                    message="Accept"
                    if(trainerC.sent_by == 0) {
                        text = "User " + trainerC.client?.name + " " + trainerC.client?.surname + " has accepted your request."
                        user = trainerC.trainer.user_id
                        text2 = "You have accepted" + trainerC.client?.name + " " + trainerC.client?.surname + "'s request."
                        user = trainerC.trainer.user_id

                    }
                    else {
                        text = "Trainer " + trainerC.trainer.user.name + " " + trainerC.trainer.user.surname + " has accepted your request."
                        user = trainerC.client_id
                        text2 = "You have accepted" + trainerC.client?.name + " " + trainerC.client?.surname + "'s request."
                        user = trainerC.client_id
                    }
                }
                else if(trainerClient.response == 2) {
                    message="Deny"
                    if(trainerC.sent_by == 0) {
                        text = "User " + trainerC.client?.name + " " + trainerC.client?.surname + " has denied your request."
                        user = trainerC.trainer.user_id
                        text2 = "You have denied" + trainerC.client?.name + " " + trainerC.client?.surname + "'s request."
                        user = trainerC.trainer.user_id
                    }
                    else {
                        text = "Trainer " + trainerC.trainer.user.name + " " + trainerC.trainer.user.surname + " has denied your request."
                        user = trainerC.client_id
                        text2 = "You have denied" + trainerC.client?.name + " " + trainerC.client?.surname + "'s request."
                        user = trainerC.client_id
                    }
                }
                val n = Notification(notification_text = text)
                val n2 = Notification(notification_text = text2)
                trainerClientRepository.addNotification(text, n.notification_time_ts)
                trainerClientRepository.addTrainerNotification(trainerC.id, trainerClientRepository.getMaxNId(), user)
                trainerClientRepository.addNotification(text2, n2.notification_time_ts)
                trainerClientRepository.addTrainerNotification(trainerC.id, trainerClientRepository.getMaxNId(), user2)
            }
            else if(trainerC.end_time_ts != trainerClient.end_time_ts) {
                message="Terminated trainer client relationship"
                text = "Your trainer client relationship has been terminated."
                val n = Notification(notification_text = text)
                trainerClientRepository.addNotification(text, n.notification_time_ts)
                trainerClientRepository.addTrainerNotification(trainerC.id, trainerClientRepository.getMaxNId(), trainerC.trainer.user_id)
                trainerClientRepository.addNotification(text, n.notification_time_ts)
                trainerClientRepository.addTrainerNotification(trainerC.id, trainerClientRepository.getMaxNId(), trainerC.client_id)
            }

            ResponseEntity.ok().body(JsonResponse(message))
        }.orElse(ResponseEntity.notFound().build())
    }
}