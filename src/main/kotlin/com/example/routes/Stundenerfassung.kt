// HoursRoutes.kt
package com.example.routes

import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import com.example.tables.Hours

// Exposed-Funktionen und Operatoren importieren
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

fun Route.hoursRoutes() {
    route("/projects/{project_id}/hours") {
        // Stunden für ein Projekt hinzufügen
        post {
            val projectId = call.parameters["project_id"]?.toIntOrNull()
            if (projectId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid project ID")
                return@post
            }

            val params = call.receiveParameters()
            val employeeId = params["employee_id"]?.toIntOrNull()
            val hours = params["hours"]?.toBigDecimalOrNull()
            val date = params["date"]
            val description = params["description"]

            if (employeeId == null || hours == null || date == null) {
                call.respond(HttpStatusCode.BadRequest, "Employee ID, hours, and date are required")
                return@post
            }

            transaction {
                Hours.insert {
                    it[Hours.project_id] = projectId
                    it[Hours.employee_id] = employeeId
                    it[Hours.hours] = hours
                    it[Hours.date] = date
                    it[Hours.description] = description
                }
            }
            call.respond(HttpStatusCode.Created, "Hours entry added successfully")
        }

        // Alle Stunden eines Projekts abrufen
        get {
            val projectId = call.parameters["project_id"]?.toIntOrNull()
            if (projectId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid project ID")
                return@get
            }

            val hours = transaction {
                Hours.select { Hours.project_id eq projectId }.map {
                    mapOf(
                        "id" to it[Hours.id],
                        "employee_id" to it[Hours.employee_id],
                        "hours" to it[Hours.hours],
                        "date" to it[Hours.date],
                        "description" to it[Hours.description]
                    )
                }
            }
            call.respond(hours)
        }

        // Einzelnen Stundeneintrag abrufen
        get("{hours_id}") {
            val projectId = call.parameters["project_id"]?.toIntOrNull()
            val hoursId = call.parameters["hours_id"]?.toIntOrNull()

            if (projectId == null || hoursId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid project or hours ID")
                return@get
            }

            val hourEntry = transaction {
                Hours.select { (Hours.project_id eq projectId) and (Hours.id eq hoursId) }
                    .map {
                        mapOf(
                            "id" to it[Hours.id],
                            "employee_id" to it[Hours.employee_id],
                            "hours" to it[Hours.hours],
                            "date" to it[Hours.date],
                            "description" to it[Hours.description]
                        )
                    }
                    .singleOrNull()
            }

            if (hourEntry == null) {
                call.respond(HttpStatusCode.NotFound, "Hours entry not found")
            } else {
                call.respond(hourEntry)
            }
        }

        // Stundeneintrag aktualisieren
        put("{hours_id}") {
            val projectId = call.parameters["project_id"]?.toIntOrNull()
            val hoursId = call.parameters["hours_id"]?.toIntOrNull()

            if (projectId == null || hoursId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid project or hours ID")
                return@put
            }

            val params = call.receiveParameters()
            transaction {
                Hours.update({ (Hours.project_id eq projectId) and (Hours.id eq hoursId) }) {
                    params["employee_id"]?.toIntOrNull()?.let { employeeId -> it[Hours.employee_id] = employeeId }
                    params["hours"]?.toBigDecimalOrNull()?.let { hours -> it[Hours.hours] = hours }
                    params["date"]?.let { date -> it[Hours.date] = date }
                    params["description"]?.let { description -> it[Hours.description] = description }
                }
            }
            call.respond(HttpStatusCode.OK, "Hours entry updated successfully")
        }

        // Stundeneintrag löschen
        delete("{hours_id}") {
            val projectId = call.parameters["project_id"]?.toIntOrNull()
            val hoursId = call.parameters["hours_id"]?.toIntOrNull()

            if (projectId == null || hoursId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid project or hours ID")
                return@delete
            }

            transaction {
                Hours.deleteWhere { (Hours.project_id eq projectId) and (Hours.id eq hoursId) }
            }
            call.respond(HttpStatusCode.OK, "Hours entry deleted successfully")
        }
    }
}
