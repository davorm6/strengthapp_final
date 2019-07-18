package com.backend.strengthapp.controller

import com.backend.strengthapp.model.Exercise
import com.backend.strengthapp.model.ExerciseList
import com.backend.strengthapp.model.JsonResponse
import com.backend.strengthapp.model.Muscle
import com.backend.strengthapp.repository.ExerciseRepository
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api")
class ExerciseController(private val exerciseRepository: ExerciseRepository) {
    @GetMapping("/exercises")
    fun getAllExercises(): ExerciseList {
        return ExerciseList(exerciseRepository.getAllExercises())
    }

    @GetMapping("/exercises/{id}")
    fun getExerciseFromId(@PathVariable(value = "id") exerciseID: Long): ResponseEntity<Exercise> {
        return exerciseRepository.findById(exerciseID).map { exercise -> ResponseEntity.ok(exercise)
        }.orElse(ResponseEntity.notFound().build())
    }

    @PostMapping("/exercises")
    fun createNewExercise(@Valid @RequestBody exercise: Exercise): ResponseEntity<Any> {
        if(exerciseRepository.existsType(exercise.type_id)) {
            var muscles = exercise.muscles
            exercise.muscles = arrayListOf()
            var result = exerciseRepository.save(exercise)
            for(m: Muscle in muscles) {
                if(!exerciseRepository.existsMuscle(m.id)) return ResponseEntity.ok(JsonResponse("Muscle with specified ID (${m.id}) does not exist"))
                else {
                    System.out.println(exerciseRepository.getMaxId())
                    exerciseRepository.addExerciseMuscle(exerciseRepository.getMaxId(), m.id)
                }
            }
            return ResponseEntity.ok(result)
        }
        return ResponseEntity.ok(JsonResponse("Type with specified ID does not exist"))
    }

    @PutMapping("/exercises/{id}")
    fun approveExercise(@PathVariable(value = "id") exerciseID: Long): ResponseEntity<JsonResponse> {
        return exerciseRepository.findById(exerciseID).map { exercise ->
            val updated = exercise.copy(request = 0)
            exerciseRepository.save(updated)
            ResponseEntity.ok().body(JsonResponse("Exercise successfully approved."))
        }.orElse(ResponseEntity.notFound().build())
    }

    @PostMapping("/exercises/search")
    fun searchExercises(@Valid @RequestBody body: JsonNode): ResponseEntity<ExerciseList> {
        val input: String = body.get("input").asText()
        if(input.isNullOrBlank() || input.isEmpty()) return ResponseEntity.ok(ExerciseList())

        val exercises = exerciseRepository.searchExercise(input);
        if(!exercises.isEmpty()) {
            return ResponseEntity.ok(ExerciseList(exercises));
        }
        return ResponseEntity.ok(ExerciseList())
    }

    @GetMapping("/exercises-request")
    fun exerciseRequests(): ResponseEntity<ExerciseList> {
        return ResponseEntity.ok().body(ExerciseList(exerciseRepository.getAllRequests()))
    }

    @DeleteMapping("/exercises/{id}")
    fun deleteExercise(@PathVariable(name = "id") exerciseID: Long): ResponseEntity<JsonResponse> {
        return exerciseRepository.findById(exerciseID).map { exercise ->
            exerciseRepository.deleteExerciseMuscle(exerciseID)
            exerciseRepository.delete(exercise)
            ResponseEntity.ok().body(JsonResponse("Exercise deleted successfully."))
        }.orElse(ResponseEntity.notFound().build())
    }
}