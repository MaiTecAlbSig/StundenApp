package com.example




import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.daoImplemenations.*
import com.example.routes.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.auth.jwt.*
import kotlinx.serialization.json.Json


fun main(args: Array<String>) {

    println("Test")

    embeddedServer(Netty, port = System.getenv("PORT")?.toInt() ?:8080, module = Application::module).start(wait = true)

}

fun Application.module() {
    configureSecurity()

    DatabaseConfig.connect()



    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    // Grundlegende Routen-Konfiguration
    routing {
        customerRoutes(CustomerDaoImpl())
        employeeRoutes(EmployeeDaoImpl())
        projectRoutes(ProjectDaoImpl())
        projectAssignmentRoutes(ProjectAssignmentDaoImpl())
        projectNoteRoutes(ProjectNoteDaoImpl())
        hourRoutes(HourDaoImpl())
        changeRequestRoutes(ChangeRequestDaoImpl())
        loginRoute()


        println("Routing done..")
    }
}

fun Application.configureSecurity() {
    val jwtSecret = System.getenv("JWT_SECRET_KEY") ?: throw IllegalStateException("JWT Secret is missing")

    install(Authentication) {
        jwt("auth-jwt") {
            realm = "ktor.io"
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("userId").asInt() != null && credential.payload.getClaim("role").asString() != null) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
}






