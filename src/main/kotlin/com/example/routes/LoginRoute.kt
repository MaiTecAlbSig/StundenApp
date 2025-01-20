package com.example.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.daoImplemenations.EmployeeDaoImpl
import com.example.datamodels.LoginRequest
import com.example.datamodels.LoginResponse
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.mindrot.jbcrypt.BCrypt
import java.util.*

fun Route.loginRoute() {
    route("/login") {

        post {
            val params = call.receiveParameters()
            val email = params["email"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Email required")
            val password =
                params["password"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Password required")
            val authUser = EmployeeDaoImpl().getAuthentication(email)
            println(email)
            println(password)
            if (authUser != null && BCrypt.checkpw(password, authUser.password_hash)) {
                val userId = authUser.id
                val isAdmin = authUser.isAdmin
                val token = generateToken(userId, isAdmin)
                call.respond(HttpStatusCode.OK, LoginResponse(token = token))
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
            }
        }
    }
}
fun generateToken(userId: Int, isAdmin: Boolean): String {
    val jwtSecret = System.getenv("JWT_SECRET_KEY") ?: throw IllegalStateException("JWT Secret is missing")

    val role = if (isAdmin) "admin" else "user"
    val secret = jwtSecret
    return JWT.create()
        .withSubject("Authentication")
        .withClaim("userId", userId)
        .withClaim("role", role) // Differenziert zwischen "admin" und "user"
        .withExpiresAt(Date(System.currentTimeMillis() + 3_600_000)) // Token 1 Stunde gültig
        .sign(Algorithm.HMAC256(secret))
}
