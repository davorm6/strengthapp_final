package com.backend.strengthapp.controller

import com.backend.strengthapp.model.Type
import com.backend.strengthapp.model.TypeList
import com.backend.strengthapp.repository.TypeRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api")
class TypeController(private val typeRepository: TypeRepository) {
    @GetMapping("/types")
    fun getAllTypes() : TypeList {
        return TypeList(typeRepository.findAll())
    }

    @PostMapping("/types")
    fun createNewType(@Valid @RequestBody type: Type): ResponseEntity<Any> {
        return ResponseEntity.ok(typeRepository.save(type))
    }
}