// ChangeRequestRoutes.kt
package com.example.routes

import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import com.example.tables.ChangeRequests

// Exposed-Funktionen und Operatoren importieren
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

 fun Route.changeRequestRoutes() {
    route("/projects/{project_id}/change-requests") {
        // Änderungsanfrage für ein Projekt erstellen
        post {
            val projectId = call.parameters["project_id"]?.toIntOrNull()
            if (projectId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid project ID")
                return@post
            }

            val params = call.receiveParameters()
            val employeeId = params["employee_id"]?.toIntOrNull()
            val requestType = params["request_type"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Request type required")
            val requestDetails = params["request_details"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Request details required")
            val status = params["status"] ?: "Pending"

            if (employeeId == null) {
                call.respond(HttpStatusCode.BadRequest, "Employee ID required")
                return@post
            }

            transaction {
                ChangeRequests.insert {
                    it[ChangeRequests.project_id] = projectId
                    it[ChangeRequests.employee_id] = employeeId
                    it[ChangeRequests.request_type] = requestType
                    it[ChangeRequests.request_details] = requestDetails
                    it[ChangeRequests.status] = status
                }
            }
            call.respond(HttpStatusCode.Created, "Change request created successfully")
        }

        // Alle Änderungsanfragen eines Projekts abrufen
        get {
            val projectId = call.parameters["project_id"]?.toIntOrNull()
            if (projectId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid project ID")
                return@get
            }

            val changeRequests = transaction {
                ChangeRequests.select { ChangeRequests.project_id eq projectId }.map {
                    mapOf(
                        "id" to it[ChangeRequests.id],
                        "employee_id" to it[ChangeRequests.employee_id],
                        "request_type" to it[ChangeRequests.request_type],
                        "request_details" to it[ChangeRequests.request_details],
                        "status" to it[ChangeRequests.status],
                        "created_at" to it[ChangeRequests.created_at],
                        "reviewed_at" to it[ChangeRequests.reviewed_at]
                    )
                }
            }
            call.respond(changeRequests)
        }

        // Einzelne Änderungsanfrage abrufen
        get("{change_request_id}") {
            val projectId = call.parameters["project_id"]?.toIntOrNull()
            val changeRequestId = call.parameters["change_request_id"]?.toIntOrNull()

            if (projectId == null || changeRequestId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid project or change request ID")
                return@get
            }

            val changeRequest = transaction {
                ChangeRequests.select {
                    (ChangeRequests.project_id eq projectId) and (ChangeRequests.id eq changeRequestId)
                }.map {
                    mapOf(
                        "id" to it[ChangeRequests.id],
                        "employee_id" to it[ChangeRequests.employee_id],
                        "request_type" to it[ChangeRequests.request_type],
                        "request_details" to it[ChangeRequests.request_details],
                        "status" to it[ChangeRequests.status],
                        "created_at" to it[ChangeRequests.created_at],
                        "reviewed_at" to it[ChangeRequests.reviewed_at]
                    )
                }.singleOrNull()
            }

            if (changeRequest == null) {
                call.respond(HttpStatusCode.NotFound, "Change request not found")
            } else {
                call.respond(changeRequest)
            }
        }

        // Änderungsanfrage aktualisieren
        put("{change_request_id}") {
            val projectId = call.parameters["project_id"]?.toIntOrNull()
            val changeRequestId = call.parameters["change_request_id"]?.toIntOrNull()

            if (projectId == null || changeRequestId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid project or change request ID")
                return@put
            }

            val params = call.receiveParameters()
            transaction {
                ChangeRequests.update({
                    (ChangeRequests.project_id eq projectId) and (ChangeRequests.id eq changeRequestId)
                }) {
                    params["employee_id"]?.toIntOrNull()?.let { employeeId -> it[ChangeRequests.employee_id] = employeeId }
                    params["request_type"]?.let { requestType -> it[ChangeRequests.request_type] = requestType }
                    params["request_details"]?.let { requestDetails -> it[ChangeRequests.request_details] = requestDetails }
                    params["status"]?.let { status -> it[ChangeRequests.status] = status }
                    params["reviewed_at"]?.let { reviewedAt -> it[ChangeRequests.reviewed_at] = reviewedAt }
                }
            }
            call.respond(HttpStatusCode.OK, "Change request updated successfully")
        }

        // Änderungsanfrage löschen
        delete("{change_request_id}") {
            val projectId = call.parameters["project_id"]?.toIntOrNull()
            val changeRequestId = call.parameters["change_request_id"]?.toIntOrNull()

            if (projectId == null || changeRequestId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid project or change request ID")
                return@delete
            }

            transaction {
                ChangeRequests.deleteWhere {
                    (ChangeRequests.project_id eq projectId) and (ChangeRequests.id eq changeRequestId)
                }
            }
            call.respond(HttpStatusCode.OK, "Change request deleted successfully")
        }
    }
}
