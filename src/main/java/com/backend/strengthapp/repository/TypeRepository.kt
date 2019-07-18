package com.backend.strengthapp.repository

import com.backend.strengthapp.model.Type
import org.springframework.data.jpa.repository.JpaRepository

interface TypeRepository: JpaRepository<Type, Long> {
}