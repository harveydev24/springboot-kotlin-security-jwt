package com.kakao.pjt.repository

import com.kakao.pjt.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, Int> {
    fun findByEmail(email: String): User?
}