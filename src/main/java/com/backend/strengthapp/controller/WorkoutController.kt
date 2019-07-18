package com.backend.strengthapp.controller

import com.backend.strengthapp.model.*
import com.backend.strengthapp.repository.ExerciseSetRepository
import com.backend.strengthapp.repository.WorkoutExerciseRepository
import com.backend.strengthapp.repository.WorkoutRepository
import com.fasterxml.jackson.databind.JsonNode
import org.apache.coyote.Response
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.sql.Timestamp
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("/api")
class WorkoutController(private val workoutRepository: WorkoutRepository)
{
    @GetMapping("/workouts")
    @ResponseBody
    fun getAllWorkouts(): WorkoutList {
        return WorkoutList(workoutRepository.findAll())
    }// = workoutRepository.findAll()

    @GetMapping("/workouts/user/{id}")
    fun getAllWorkoutsFromUserID(@PathVariable(value = "id") userID: Long) : ResponseEntity<Any> {
        if(workoutRepository.existsUser(userID)) {
            val workouts = workoutRepository.findWorkoutsByUser_id(userID)
            if(workouts.isNotEmpty()) {
                var workout_list: WorkoutList = WorkoutList(workouts)
                return ResponseEntity.ok(workout_list)
            }
            else {
                return ResponseEntity.ok(JsonResponse("No workouts for specified user."))
            }
        }
        else return ResponseEntity.ok(JsonResponse("User does not exist."))
    }

    @GetMapping("/workouts/{id}/exercises")
    fun getWorkoutExercisesFromId(@PathVariable(value = "id") workoutID: Long): ResponseEntity<WorkoutExercises> {
        return workoutRepository.findById(workoutID).map { workout -> ResponseEntity.ok(WorkoutExercises(workout.exercises))
        }.orElse(ResponseEntity.notFound().build())
    }


    @PostMapping("/workouts")
    fun createNewWorkout(@Valid @RequestBody workout: Workout): ResponseEntity<Any> {
        if (!workoutRepository.existsUser(workout.user_id)) return ResponseEntity.ok(JsonResponse("User with specified id does not exist"))

        workout.user = workoutRepository.findUser(workout.user_id)

        workout.trainer_client = null

        val result = workoutRepository.save(workout)

        if(workout.trainer_client_id != null) {
            val n = Notification(notification_text = "Your trainer added a new workout for you - click to view it.")
            workoutRepository.addNotification(n.notification_text, n.notification_time_ts)
            workoutRepository.addWorkoutNotification(workoutRepository.getMaxWId(), workoutRepository.getMaxNId(), workout.user_id)
        }

        return ResponseEntity.ok(result)
    }

    @GetMapping("/workouts/{id}")
    fun getWorkoutFromId(@PathVariable(value = "id") workoutID: Long): ResponseEntity<Workout> {
        return workoutRepository.findById(workoutID).map { workout -> ResponseEntity.ok(workout)
        }.orElse(ResponseEntity.notFound().build())
    }

    @PutMapping("/workouts/{id}")
    fun updateWorkoutFromId(@PathVariable(value = "id") workoutID: Long,
                         @Valid @RequestBody newWorkout: Workout): ResponseEntity<Workout> {

        return workoutRepository.findById(workoutID).map { workout ->
            val updatedWorkout: Workout = workout
                    .copy(user_note_id = newWorkout.user_note_id, trainer_note_id = newWorkout.trainer_note_id)
            ResponseEntity.ok().body(workoutRepository.save(updatedWorkout))
        }.orElse(ResponseEntity.notFound().build())
    }

    @PostMapping("/workouts/copy/{id}")
    fun copyWorkout(@PathVariable(value = "id") id: Long) : ResponseEntity<Workout> {
        return workoutRepository.findById(id).map { workout ->
            val newWorkout: Workout = Workout(0, workout.user_id, Timestamp(System.currentTimeMillis()), workout.user_note_id, workout.trainer_note_id, workout.trainer_client_id, 0)
            newWorkout.trainer_client = null
            newWorkout.user = null
            val result = workoutRepository.save(newWorkout)

            for(e in workoutRepository.getWorkoutExercises(id).exercises) {
                workoutRepository.addWorkoutExercise(workoutRepository.getMaxWId(), e.workoutExerciseId.exercise_id, e.ordinal_number)
                for(s in e.sets) {
                    workoutRepository.addExerciseSet(workoutRepository.getMaxWId(), e.workoutExerciseId.exercise_id, s.exerciseSetId.set_number)
                }
            }
            ResponseEntity.ok().body(result)
        }.orElse(ResponseEntity.notFound().build())
    }

    @DeleteMapping("/workouts/{id}")
    fun deleteWorkoutFromId(@PathVariable(value = "id") workoutID: Long): ResponseEntity<Void> {

        return workoutRepository.findById(workoutID).map { workout  ->
            workoutRepository.delete(workout)
            ResponseEntity<Void>(HttpStatus.OK)
        }.orElse(ResponseEntity.notFound().build())
    }
}