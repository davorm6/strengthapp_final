package com.backend.strengthapp.repository

import com.backend.strengthapp.model.Muscle
import org.springframework.data.jpa.repository.JpaRepository

interface MuscleRepository: JpaRepository<Muscle, Long> {
}