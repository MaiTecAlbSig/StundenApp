// ProjectAssignmentRoutes.kt
package com.example.routes

import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import com.example.tables.ProjectAssignments

// Exposed-Funktionen und Operatoren importieren
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

fun Route.projectAssignmentRoutes() {
    route("/project-assignments") {
        // Mitarbeiter einem Projekt zuweisen
        post {
            val params = call.receiveParameters()
            val projectId = params["project_id"]?.toIntOrNull()
            val employeeId = params["employee_id"]?.toIntOrNull()
            val role = params["role"]

            if (projectId == null || employeeId == null) {
                call.respond(HttpStatusCode.BadRequest, "Project ID and Employee ID are required")
                return@post
            }

            transaction {
                ProjectAssignments.insert {
                    it[ProjectAssignments.project_id] = projectId
                    it[ProjectAssignments.employee_id] = employeeId
                    it[ProjectAssignments.role] = role
                }
            }
            call.respond(HttpStatusCode.Created, "Assignment created successfully")
        }

        // Alle Zuweisungen abrufen
        get {
            val assignments = transaction {
                ProjectAssignments.selectAll().map {
                    mapOf(
                        "project_id" to it[ProjectAssignments.project_id],
                        "employee_id" to it[ProjectAssignments.employee_id],
                        "role" to it[ProjectAssignments.role]
                    )
                }
            }
            call.respond(assignments)
        }

        // Abrufen einer bestimmten Zuweisung
        get("/{project_id}/{employee_id}") {
            val projectId = call.parameters["project_id"]?.toIntOrNull()
            val employeeId = call.parameters["employee_id"]?.toIntOrNull()

            if (projectId == null || employeeId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid Project ID or Employee ID")
                return@get
            }

            val assignment = transaction {
                ProjectAssignments.select {
                    (ProjectAssignments.project_id eq projectId) and (ProjectAssignments.employee_id eq employeeId)
                }.map {
                    mapOf(
                        "project_id" to it[ProjectAssignments.project_id],
                        "employee_id" to it[ProjectAssignments.employee_id],
                        "role" to it[ProjectAssignments.role]
                    )
                }.singleOrNull()
            }

            if (assignment == null) {
                call.respond(HttpStatusCode.NotFound, "Assignment not found")
            } else {
                call.respond(assignment)
            }
        }

        // Zuweisung aktualisieren (Rolle ändern)
        put("/{project_id}/{employee_id}") {
            val projectId = call.parameters["project_id"]?.toIntOrNull()
            val employeeId = call.parameters["employee_id"]?.toIntOrNull()

            if (projectId == null || employeeId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid Project ID or Employee ID")
                return@put
            }

            val params = call.receiveParameters()
            val role = params["role"]

            transaction {
                ProjectAssignments.update({
                    (ProjectAssignments.project_id eq projectId) and (ProjectAssignments.employee_id eq employeeId)
                }) {
                    it[ProjectAssignments.role] = role
                }
            }
            call.respond(HttpStatusCode.OK, "Assignment updated successfully")
        }

        // Zuweisung löschen
        delete("/{project_id}/{employee_id}") {
            val projectId = call.parameters["project_id"]?.toIntOrNull()
            val employeeId = call.parameters["employee_id"]?.toIntOrNull()

            if (projectId == null || employeeId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid Project ID or Employee ID")
                return@delete
            }

            transaction {
                ProjectAssignments.deleteWhere {
                    (ProjectAssignments.project_id eq projectId) and (ProjectAssignments.employee_id eq employeeId)
                }
            }
            call.respond(HttpStatusCode.OK, "Assignment deleted successfully")
        }
    }
}
