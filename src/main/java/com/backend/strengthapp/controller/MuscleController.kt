package com.backend.strengthapp.controller

import com.backend.strengthapp.model.Muscle
import com.backend.strengthapp.model.MuscleList
import com.backend.strengthapp.repository.MuscleRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api")
class MuscleController(private val muscleRepository: MuscleRepository) {
    @GetMapping("/muscles")
    fun getAllMuscles() : MuscleList {
        return MuscleList(muscleRepository.findAll())
    }

    @PostMapping("/muscles")
    fun createNewMuscle(@Valid @RequestBody muscle: Muscle): ResponseEntity<Any> {
        return ResponseEntity.ok(muscleRepository.save(muscle))
    }
}