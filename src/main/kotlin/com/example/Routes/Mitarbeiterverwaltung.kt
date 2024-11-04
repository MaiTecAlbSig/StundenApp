// EmployeeRoutes.kt
package com.example.Routes

import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import com.example.tables.Employees
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.mindrot.jbcrypt.BCrypt

fun Route.employeeRoutes() {
    route("/employees") {
        // Mitarbeiter erstellen
        post {
            val params = call.receiveParameters()
            val name = params["name"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Name required")
            val position = params["position"]
            val email = params["email"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Email required")
            val password = params["password_hash"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Password required")
            val isAdmin = params["is_admin"]?.toBoolean() ?: false

            val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt())

            transaction {
                Employees.insert {
                    it[Employees.name] = name
                    it[Employees.position] = position
                    it[Employees.email] = email
                    it[Employees.password_hash] = passwordHash
                    it[Employees.isAdmin] = isAdmin
                }
            }
            call.respond(HttpStatusCode.Created, "Employee created successfully")
        }

        // Alle Mitarbeiter abrufen
        get {
            val employees = transaction {
                Employees.selectAll().map {
                    mapOf(
                        "id" to it[Employees.id],
                        "name" to it[Employees.name],
                        "position" to it[Employees.position],
                        "email" to it[Employees.email]
                    )
                }
            }
            call.respond(employees)
        }

        // Mitarbeiter nach ID abrufen
        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid employee ID")
                return@get
            }
            val employee = transaction {
                Employees.select { Employees.id eq id }.map {
                    mapOf(
                        "id" to it[Employees.id],
                        "name" to it[Employees.name],
                        "position" to it[Employees.position],
                        "email" to it[Employees.email]
                    )
                }.singleOrNull()
            }
            if (employee == null) {
                call.respond(HttpStatusCode.NotFound, "Employee not found")
            } else {
                call.respond(employee)
            }
        }

        // Mitarbeiter aktualisieren
        put("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid employee ID")
                return@put
            }
            val params = call.receiveParameters()
            transaction {
                Employees.update({ Employees.id eq id }) {
                    params["name"]?.let { name -> it[Employees.name] = name }
                    params["position"]?.let { position -> it[Employees.position] = position }
                    params["email"]?.let { email -> it[Employees.email] = email }
                    params["password_hash"]?.let { passwordHash -> it[Employees.password_hash] = passwordHash }
                }
            }
            call.respond(HttpStatusCode.OK, "Employee updated successfully")
        }

        // Mitarbeiter löschen
        delete("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid employee ID")
                return@delete
            }
            transaction {
                Employees.deleteWhere { Employees.id eq id }
            }
            call.respond(HttpStatusCode.OK, "Employee deleted successfully")
        }
    }
}
