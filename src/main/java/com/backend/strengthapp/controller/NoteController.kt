package com.backend.strengthapp.controller

import com.backend.strengthapp.model.*
import com.backend.strengthapp.repository.NoteRepository
import com.backend.strengthapp.repository.TypeRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api")
class NoteController(private val noteRepository: NoteRepository) {
    @GetMapping("/notes")
    fun getAllNotes() : WorkoutNoteList {
        return WorkoutNoteList(noteRepository.findAll())
    }

    @PostMapping("/notes")
    fun createNewNote(@Valid @RequestBody note: WorkoutNote): ResponseEntity<Any> {
        if(note.notification_id == null || noteRepository.existsNotification(note.notification_id)) {
            if(noteRepository.existsUser(note.user_id)) {
                return ResponseEntity.ok(noteRepository.save(note))
            }
            else {
                return ResponseEntity.ok(JsonResponse("User with specified ID does not exist."))
            }
        }
        else {
            return ResponseEntity.ok(JsonResponse("Notification with specified ID does not exist."))
        }
    }

    @PutMapping("/notes/{id}")
    fun editNote(@PathVariable("id") id: Long, @Valid @RequestBody note: WorkoutNote): ResponseEntity<WorkoutNote> {
        return noteRepository.findById(id).map { o_note ->
            val newNote: WorkoutNote = o_note.copy(note = note.note)
            ResponseEntity.ok().body(noteRepository.save(newNote))
        }.orElse(ResponseEntity.notFound().build())
    }
}