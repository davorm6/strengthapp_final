package com.backend.strengthapp.model

import javax.validation.constraints.NotBlank

data class JsonResponse(
        @get: NotBlank
        val message: String,
        val id: Long = 0L)