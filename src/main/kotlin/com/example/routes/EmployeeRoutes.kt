package com.example.routes


import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.daointerfaces.EmployeeDao
import com.example.datamodels.LoginResponse
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

import org.mindrot.jbcrypt.BCrypt
import java.util.*

fun Route.employeeRoutes(employeeDao: EmployeeDao) {
    authenticate("auth-jwt") {
        route("/employee") {
            post {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt() ?: error("userId darf nicht null sein")
                val role = principal?.payload?.getClaim("role")?.asString() ?: error("role darf nicht null sein")

                val params = call.receiveParameters()
                val name = params["name"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Name required")
                val position =
                    params["position"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Position required")
                val email = params["email"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Email required")
                val password =
                    params["password_hash"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Password required")
                val isAdmin = params["is_admin"]?.toBoolean() ?: false
                val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt())

                val article = employeeDao.createEmployee(name, position, email, passwordHash, isAdmin)

                call.respond(HttpStatusCode.Created, "Employee created successfully")
            }


            get {
                call.respond(HttpStatusCode.OK, employeeDao.allEmployees())
            }


            get("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid employee ID")
                    return@get
                }
                val employee = employeeDao.getEmployee(id)
                if (employee == null) {
                    call.respond(HttpStatusCode.NotFound, "Employee not found")
                } else {
                    call.respond(employee)
                }
            }

            put("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid employee ID")
                    return@put
                }
                val params = call.receiveParameters()
                val article = employeeDao.updateEmployee(
                    id,
                    params["name"],
                    params["position"],
                    params["email"],
                    params["password_hash"],
                    params["isAdmin"].toBoolean()
                )

                call.respond(HttpStatusCode.OK, "Employee updated successfully")

            }
            delete("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid employee ID")
                    return@delete
                }
                employeeDao.deleteEmployee(id)
                call.respond(HttpStatusCode.OK, "Employee deleted successfully")
            }
        }
    }
}

