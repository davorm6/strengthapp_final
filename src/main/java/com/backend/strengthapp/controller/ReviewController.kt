package com.backend.strengthapp.controller

import com.backend.strengthapp.model.*
import com.backend.strengthapp.repository.ReviewRepository
import com.backend.strengthapp.repository.TypeRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api")
class ReviewController(private val reviewRepository: ReviewRepository) {
    @GetMapping("/reviews")
    fun getAllReviews() : TrainerReviewList {
        return TrainerReviewList(reviewRepository.findAll())
    }

    @GetMapping("/reviews/{id}")
    fun getReview(@PathVariable("id") id: Long): ResponseEntity<TrainerReview> {
        return reviewRepository.findById(id).map {review ->
            ResponseEntity.ok().body(review)
        }.orElse(ResponseEntity.notFound().build())
    }

    @GetMapping("/reviews/trainer/{id}")
    fun getTrainerReviews(@PathVariable("id") id: Long): ResponseEntity<Any> {
        val list: MutableList<TrainerReview> = mutableListOf()
        for(review in reviewRepository.findAll()) {
            if(review.trainerClient.trainer_id == id) {
                list.add(review)
            }
        }
        return ResponseEntity.ok(TrainerReviewList(list))
    }

    @PostMapping("/reviews")
    fun createNewType(@Valid @RequestBody review: TrainerReview): ResponseEntity<Any> {
        if(reviewRepository.existsNotification(review.notification_id)) {
            if(reviewRepository.existsTrainerClient(review.trainer_client_id)) {
                if(reviewRepository.existsReviewForTrainerClient(review.trainer_client_id)) return ResponseEntity.ok(reviewRepository.save(review))
                else return ResponseEntity.ok(JsonResponse("Already exists a review for this trainer-client relationship."))
            }
            else return ResponseEntity.ok(JsonResponse("TrainerClient with specified ID does not exist."))
        }
        else return ResponseEntity.ok(JsonResponse("Notification with specified ID does not exist."))
    }
}