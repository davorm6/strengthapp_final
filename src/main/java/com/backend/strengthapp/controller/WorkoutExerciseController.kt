package com.backend.strengthapp.controller

import com.backend.strengthapp.model.*
import com.backend.strengthapp.repository.WorkoutExerciseRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api")
class WorkoutExerciseController (private val workoutExerciseRepository: WorkoutExerciseRepository) {
    @PostMapping("workouts/exercises")
    fun createNewExercise(@Valid @RequestBody workoutExercise: WorkoutExercise): ResponseEntity<Any> {
        val workID = workoutExercise.workoutExerciseId.workout_id
        val exeID = workoutExercise.workoutExerciseId.exercise_id
        if(workoutExerciseRepository.existsWorkout(workoutExercise.workoutExerciseId?.workout_id!!)) {
            if(workoutExerciseRepository.existsExercise(workoutExercise.workoutExerciseId?.exercise_id!!)) {
                val workout_exercise = workoutExerciseRepository.findById(WorkoutExerciseId(workID, exeID))
                if(!workout_exercise.isPresent) {
                    workoutExercise.exercise = workoutExerciseRepository.getExerciseFromId(exeID)
                    workoutExerciseRepository.save(workoutExercise)
                    return ResponseEntity.ok(JsonResponse("Exercises added successfully."))
                }
                return ResponseEntity.ok(JsonResponse("Exercise already added to selected workout. Try adding new sets to the exercise."))
            }
            return ResponseEntity.ok(JsonResponse("Exercise with specified ID ($exeID) does not exist"))
        }
        return ResponseEntity.ok(JsonResponse("Workout with specified ID ($workID) does not exist"))
    }

    @DeleteMapping("workouts/{id}/exercises/{eid}")
    fun deleteWorkoutExercise(@PathVariable("id") workout_id: Long, @PathVariable("eid") exercise_id: Long) : ResponseEntity<Any> {
        val workout = workoutExerciseRepository.findById(WorkoutExerciseId(workout_id, exercise_id))
        if(workout.isPresent) {
            workoutExerciseRepository.clearExerciseSets(workout_id, exercise_id)
            workoutExerciseRepository.deleteWorkoutExercise(workout_id, exercise_id)
            return ResponseEntity.ok(JsonResponse("Exercise removed successfully."))
        }
        else {
            return ResponseEntity.ok(JsonResponse("Exercise does not exist."))
        }
        /*return workoutExerciseRepository.findById(WorkoutExerciseId(workout_id, exercise_id)).map { workout  ->
            workoutExerciseRepository.clearExerciseSets(workout_id, exercise_id)
            workoutExerciseRepository.deleteWorkoutExercise(workout_id, exercise_id)
            ResponseEntity.ok(JsonResponse("Exercise removed successfully."))
        }.orElse(ResponseEntity.notFound().build())*/
    }
}