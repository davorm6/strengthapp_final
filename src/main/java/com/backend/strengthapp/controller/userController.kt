package com.backend.strengthapp.controller

import com.backend.strengthapp.model.JsonResponse
import com.backend.strengthapp.model.User
import com.backend.strengthapp.model.UserList
import com.backend.strengthapp.repository.UserRepository
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api")
class userController(private val userRepository: UserRepository)
{
    @GetMapping("/users")
    @ResponseBody
    fun getAllUsers(): UserList {
        return UserList(userRepository.findAll())
    }// = userRepository.findAll()

    @PostMapping("/users")
    fun createNewUser(@Valid @RequestBody user: User): ResponseEntity<Any> {
        if (userRepository.existsUserByE_mail(user.e_mail)) return ResponseEntity.ok(JsonResponse("User with specified e-mail already exists"))

        return ResponseEntity.ok(userRepository.save(user))
    }

    @GetMapping("/users/{id}")
    fun getUserFromId(@PathVariable(value = "id") userID: Long): ResponseEntity<User> {
        return userRepository.findById(userID).map { user ->
            ResponseEntity.ok(user)
        }.orElse(ResponseEntity.notFound().build())
    }

    @PutMapping("/users/{id}")
    fun updateUserFromId(@PathVariable(value = "id") userID: Long,
                          @Valid @RequestBody newUser: User): ResponseEntity<User> {

        return userRepository.findById(userID).map { user ->
            val updatedUser: User = user
                    .copy(e_mail = newUser.e_mail, name = newUser.name, surname = newUser.surname, password = newUser.password, mail_confirm = newUser.mail_confirm)
            ResponseEntity.ok().body(userRepository.save(updatedUser))
        }.orElse(ResponseEntity.notFound().build())

    }

    @DeleteMapping("/users/{id}")
    fun deleteUserFromId(@PathVariable(value = "id") userID: Long): ResponseEntity<Void> {

        return userRepository.findById(userID).map { user  ->
            userRepository.delete(user)
            ResponseEntity<Void>(HttpStatus.OK)
        }.orElse(ResponseEntity.notFound().build())
    }

    @GetMapping("/users/mail")
    fun getUserFromMail(@Valid @RequestBody body: JsonNode) : ResponseEntity<Any> {
        val mail: String = body.get("mail").asText()
        if(mail.isNullOrBlank() || mail.isEmpty()) return ResponseEntity.notFound().build()

        if(userRepository.existsUserByE_mail(mail)) return ResponseEntity.ok().body(userRepository.findUserByMail(mail))

        return ResponseEntity.ok(JsonResponse("There are no users with the specified e-mail address."))
    }

    @PostMapping("/users/search")
    fun getUserFromMailName(@Valid @RequestBody body: JsonNode) : ResponseEntity<Any> {
        val mail: String = body.get("mail").asText()
        if(mail.isNullOrBlank() || mail.isEmpty()) return ResponseEntity.notFound().build()

        if(userRepository.existsUserByE_mailName(mail)) return ResponseEntity.ok().body(userRepository.findUserByMailName(mail))

        return ResponseEntity.ok(JsonResponse("There are no users with the specified e-mail address."))
    }

    @PostMapping("/users/login")
    fun checkUserLogin(@Valid @RequestBody body: JsonNode) : ResponseEntity<Any> {
        val mail: String = body.get("mail").asText()
        if(mail.isNullOrBlank() || mail.isEmpty()) return ResponseEntity.notFound().build()
        val password: String = body.get("password").asText()
        if(password.isNullOrBlank() || password.isEmpty()) return ResponseEntity.notFound().build()

        if (userRepository.existsUserByE_mail(mail)) {
            val user = userRepository.findUserByMail(mail)
            if(user.password.equals(password)) return ResponseEntity.ok(JsonResponse("Accept", user.id))
            else return ResponseEntity.ok(JsonResponse("Password you entered does not match the account password."))
        }
        else return ResponseEntity.ok(JsonResponse("User with that e-mail does not exist."))
    }
}