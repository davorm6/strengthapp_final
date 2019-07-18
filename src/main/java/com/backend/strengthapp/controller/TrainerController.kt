package com.backend.strengthapp.controller

import com.backend.strengthapp.model.*
import com.backend.strengthapp.repository.TrainerRepository
import com.backend.strengthapp.repository.UserRepository
import com.fasterxml.jackson.databind.JsonNode
import org.apache.coyote.Response
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("/api")
class TrainerController(private val trainerRepository: TrainerRepository)
{
    @GetMapping("/trainers")
    fun getAllTrainers() : TrainerList {
        return TrainerList(trainerRepository.findAll())
    }

    @PostMapping("/trainers")
    fun createNewTrainer(@Valid @RequestBody trainer: Trainer): ResponseEntity<Any> {
        if(trainerRepository.existsUser(trainer.user_id)) {
            trainer.user = trainerRepository.findUser(trainer.user_id)
            return ResponseEntity.ok(trainerRepository.save(trainer))
        }
        return ResponseEntity.ok(JsonResponse("User with specified ID does not exist"))
    }

    @GetMapping("/trainers/{id}")
    fun getTrainerFromId(@PathVariable(value = "id") trainerID: Long): ResponseEntity<Trainer> {
        return trainerRepository.findById(trainerID).map { trainer -> ResponseEntity.ok(trainer)
        }.orElse(ResponseEntity.notFound().build())
    }

    @GetMapping("/trainers/user/{id}")
    fun checkUserTrainer(@PathVariable(value = "id") userID: Long): ResponseEntity<Trainer> {
        if(trainerRepository.isUserTrainer(userID)) {
            return ResponseEntity.ok(trainerRepository.findTrainerFromUser(userID))
        }
        return ResponseEntity.notFound().build()
    }

    @PutMapping("/trainers/{id}")
    fun updateTrainerFromId(@PathVariable(value = "id") trainerID: Long, @Valid @RequestBody newTrainer: Trainer) : ResponseEntity<Trainer> {
        return trainerRepository.findById(trainerID).map { trainer ->
            val updatedTrainer: Trainer = trainer.copy(certificate = newTrainer.certificate, website = newTrainer.website, about_me = newTrainer.about_me, profile_photo = newTrainer.profile_photo)
            ResponseEntity.ok().body(trainerRepository.save(updatedTrainer))
        }.orElse(ResponseEntity.notFound().build())
    }

    @DeleteMapping("/trainers/{id}")
    fun deleteTrainerFromId(@PathVariable(value = "id") trainerID: Long): ResponseEntity<Void> {
        return trainerRepository.findById(trainerID).map { trainer ->
            trainerRepository.delete(trainer)
            ResponseEntity<Void>(HttpStatus.OK)
        }.orElse(ResponseEntity.notFound().build())
    }
}