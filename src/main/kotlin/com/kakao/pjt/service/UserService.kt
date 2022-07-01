package com.kakao.pjt.service

import com.kakao.pjt.model.User
import com.kakao.pjt.repository.UserRepository
import org.springframework.stereotype.Service
import javax.persistence.EntityManager

@Service
class UserService(private val userRepository: UserRepository) {

        fun save(user: User): User = this.userRepository.save(user)

        fun findByEmail(email: String): User? = this.userRepository.findByEmail(email)

        fun getById(id: Int): User? = this.userRepository.getById(id)
}