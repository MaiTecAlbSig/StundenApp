package com.example.routes

import com.example.daoImplemenations.EmployeeDaoImpl
import com.example.datamodels.LoginResponse
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.mindrot.jbcrypt.BCrypt

fun Route.loginRoute() {
    route("/login") {

        get {
            val params = call.receiveParameters()
            val email = params["email"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Email required")
            val password =
                params["password"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Password required")
            val authUser = EmployeeDaoImpl().getAuthentication(email)

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