package com.kakao.pjt.controller

import com.kakao.pjt.dto.LoginDto
import com.kakao.pjt.dto.Message
import com.kakao.pjt.dto.RegisterDto
import com.kakao.pjt.model.User
import com.kakao.pjt.service.UserService
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("api/v1")
class AuthController(private val userService: UserService) {

    val secretKey = "thisismysecretkeyyouarenotabletoseethistoolooooooooong"

    @PostMapping("register")
    fun register(@RequestBody registerDto: RegisterDto): ResponseEntity<User> {
        val user = User()
        user.name = registerDto.name
        user.email = registerDto.email
        user.password = registerDto.password
        return ResponseEntity.ok(this.userService.save(user))
    }

    @PostMapping("login")
    fun login(@RequestBody loginDto: LoginDto, response: HttpServletResponse): ResponseEntity<Any> {
        val user = this.userService.findByEmail(loginDto.email)
            ?: return ResponseEntity.badRequest().body(Message("USER NOT FOUND"))

        if(!user.comparePassword(loginDto.password)) {
            return ResponseEntity.badRequest().body(Message("INVALID PASSWORD"))
        }

        val issuer = user.id.toString()


        val jwt = Jwts.builder()
            .setIssuer(issuer)
            .setExpiration(Date(System.currentTimeMillis() + 60 * 24 * 1000))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()

        val cookie = Cookie("jwt", jwt)
        cookie.isHttpOnly = true

        response.addCookie(cookie)

        return ResponseEntity.ok(Message("success"))
    }

    @GetMapping("user")
    fun user(@CookieValue("jwt") jwt: String): ResponseEntity<Any> {
        try {
            if(jwt == null) {
                return ResponseEntity.status(401).body(Message("unauthenticated"))
            }

            val body = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt).body

            return ResponseEntity.ok(this.userService.getById(body.issuer.toInt()))
        } catch (e: Exception) {
            return ResponseEntity.status(401).body(Message("unauthenticated"))
        }
    }

    @GetMapping("logout")
    fun logout(response: HttpServletResponse): ResponseEntity<Any> {
        var cookie = Cookie("jwt", "")
        cookie.maxAge = 0

        response.addCookie(cookie)

        return ResponseEntity.ok(Message("logout"))
    }
}