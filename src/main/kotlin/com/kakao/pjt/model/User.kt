package com.kakao.pjt.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int = 0

    @Column
    var name: String = ""

    @Column(unique = true)
    var email: String = ""

    @Column
    var password: String = ""
        @JsonIgnore
        get() = field
        set(value) {
            val passwordEncoder = BCryptPasswordEncoder()
            field = passwordEncoder.encode(value)
        }

    fun comparePassword(password: String): Boolean {
        return BCryptPasswordEncoder().matches(password, this.password)
    }
}