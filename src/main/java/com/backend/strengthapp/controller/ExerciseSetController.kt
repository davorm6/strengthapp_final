package com.backend.strengthapp.controller

import com.backend.strengthapp.model.*
import com.backend.strengthapp.repository.ExerciseSetRepository
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api")
class ExerciseSetController (private val exerciseSetRepository: ExerciseSetRepository) {
    @PostMapping("workouts/exercises/sets")
    fun createNewSet(@Valid @RequestBody exerciseSet: ExerciseSet): ResponseEntity<Any> {
        val workID = exerciseSet.exerciseSetId.workoutExerciseId.workout_id
        val exeID = exerciseSet.exerciseSetId.workoutExerciseId.exercise_id
        if(exerciseSetRepository.existsWorkout(workID)) {
            if(exerciseSetRepository.existsExercise(exeID)) {
                return ResponseEntity.ok(exerciseSetRepository.save(exerciseSet))
            }
            return ResponseEntity.ok(JsonResponse("Exercise with specified ID ($exeID) does not exist"))
        }
        return ResponseEntity.ok(JsonResponse("Workout with specified ID ($workID) does not exist"))
    }

    @PostMapping("/workouts/exercises/sets/edit")
    fun updateExerciseSet(@Valid @RequestBody body: JsonNode) : ResponseEntity<ExerciseSet> {
        if(!(body.has("workoutID") && body.has("exerciseID") && body.has("setID"))) {
            return ResponseEntity.notFound().build()
        }
        val wid: Int = body.get("workoutID").asInt()
        val eid: Int = body.get("exerciseID").asInt()
        val sid: Int = body.get("setID").asInt()
        var weight: Int? = null
        var reps: Int? = null
        if(body.has("weight")) weight = body.get("weight").asInt()
        if(body.has("repetitions")) reps = body.get("repetitions").asInt()

        val set = exerciseSetRepository.findById(ExerciseSetId(WorkoutExerciseId(wid.toLong(), eid.toLong()), sid.toLong()))

        var w = 0
        if(weight != null) w = weight
        var r = 0
        if(reps != null) r = reps
        if(set.isPresent) {
            val setvalue = set.get()
            if(weight == null) w = setvalue.weight
            if(reps == null) r = setvalue.repetitions
            return set.map { newset ->
                val updatedSet: ExerciseSet = newset.copy(repetitions = r, weight = w)
                ResponseEntity.ok().body(exerciseSetRepository.save(updatedSet))
            }.orElse(ResponseEntity.notFound().build())
        }
        return ResponseEntity.notFound().build()
    }

    @PostMapping("/workouts/exercises/sets/del")
    fun deleteExerciseSet(@Valid @RequestBody body: JsonNode) : ResponseEntity<Any> {
        val wid: Int = body.get("workoutID").asInt()
        val eid: Int = body.get("exerciseID").asInt()
        val sid: Int = body.get("setID").asInt()
        if(wid != null && eid != null && sid != null) {
            val set = exerciseSetRepository.findById(ExerciseSetId(WorkoutExerciseId(wid.toLong(), eid.toLong()), sid.toLong()))

            if(set.isPresent) {
                exerciseSetRepository.delete(set.get())
                return ResponseEntity.ok().body(JsonResponse("Set successfully deleted."))
            }
        }
        return ResponseEntity.ok().body(JsonResponse("Set could not be found."))
    }
}