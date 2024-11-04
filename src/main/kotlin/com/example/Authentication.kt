package com.example

// Authentication.kt
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.post
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.tables.Employees
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.util.*

// Data class to receive login data
@Serializable
data class LoginRequest(val email: String, val password: String)
@Serializable
data class LoginResponse(val token: String)


fun Route.loginRoute() {
    post("/login") {
        val credentials = call.receive<LoginRequest>()

        // Mitarbeiter mit der angegebenen E-Mail in der Datenbank suchen
        val user = transaction {
            Employees.select { Employees.email eq credentials.email }
                .map { Triple(it[Employees.password_hash], it[Employees.id], it[Employees.isAdmin]) }
                .singleOrNull()
        }

        // Überprüfen, ob Benutzer gefunden wurde und Passwort korrekt ist
        if (user != null && BCrypt.checkpw(credentials.password, user.first)) {
            val token = generateToken(user.second, user.third) // Token mit Benutzer-ID und Admin-Status erstellen
            call.respond(LoginResponse(token = token))
        } else {
            call.respond(HttpStatusCode.Unauthorized, "Invalid email or password")
        }
    }
}

// Funktion zur Token-Generierung mit Unterscheidung zwischen Admin und User
fun generateToken(userId: Int, isAdmin: Boolean): String {
    val jwtSecret = System.getenv("JWT_SECRET_KEY") ?: throw IllegalStateException("JWT Secret is missing")

    val role = if (isAdmin) "admin" else "user"
    val secret = jwtSecret
    return JWT.create()
        .withClaim("userId", userId)
        .withClaim("role", role) // Differenziert zwischen "admin" und "user"
        .withExpiresAt(Date(System.currentTimeMillis() + 3_600_000)) // Token 1 Stunde gültig
        .sign(Algorithm.HMAC256(secret))
}
