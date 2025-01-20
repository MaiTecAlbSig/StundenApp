package com.example.routes

import com.example.daointerfaces.ChangeRequestDao
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun Route.changeRequestRoutes(changeRequestDao: ChangeRequestDao) {
    authenticate("auth-jwt") {
        route("/change-request") {

            post {
                val params = call.receiveParameters()
                val projectId =
                    params["project_id"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Project ID required")
                val employeeId =
                    params["employee_id"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Employee ID required")
                val requestType = params["request_type"] ?: return@post call.respond(
                    HttpStatusCode.BadRequest,
                    "Request type required"
                )
                val requestDescription = params["request_description"] ?: return@post call.respond(
                    HttpStatusCode.BadRequest,
                    "Request description required"
                )
                val currentDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

                val article = changeRequestDao.addChangeRequest(
                    projectId.toInt(),
                    employeeId.toInt(),
                    requestType,
                    requestDescription,
                    currentDate
                )
                call.respond(HttpStatusCode.Created, "Change request created successfully")
            }

            get {
                call.respond(HttpStatusCode.OK, changeRequestDao.getChangeRequests())
            }

            get("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid change request ID")
                    return@get
                }
                val changeRequest = changeRequestDao.getChangeRequestById(id)
                if (changeRequest == null) {
                    call.respond(HttpStatusCode.NotFound, "Change request not found")
                } else {
                    call.respond(changeRequest)
                }
            }

            get("/employee/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid employee ID")
                    return@get
                }
                val employeeChangeRequest = changeRequestDao.getChangeRequestsByEmployeeId(id)
                if (employeeChangeRequest == null) {
                    call.respond(HttpStatusCode.NotFound, "Employee not found")
                } else {
                    call.respond(employeeChangeRequest)
                }
            }

            get("/project/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid project ID")
                    return@get
                }
                val projectChangeRequest = changeRequestDao.getChangeRequestsByProjectId(id)
                if (projectChangeRequest == null) {
                    call.respond(HttpStatusCode.NotFound, "Project not found")
                } else {
                    call.respond(projectChangeRequest)
                }
            }

            get("/status/{status}") {
                val status =
                    call.parameters["status"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Status required")
                val statusChangeRequest = changeRequestDao.getChangeRequestsByStatus(status)
                if (statusChangeRequest == null) {
                    call.respond(HttpStatusCode.NotFound, "Change request not found")
                } else {
                    call.respond(statusChangeRequest)
                }
            }

            put("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid change request ID")
                    return@put
                }
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString() ?: error("role darf nicht null sein")
                if (role != "admin") {
                    call.respond(HttpStatusCode.Forbidden, "Admin access required")
                    return@put
                }

                val params = call.receiveParameters()
                val currentDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                val article = changeRequestDao.updateChangeRequest(
                    id,
                    params["requestType"],
                    params["requestDescription"],
                    params["status"],
                    currentDate
                )
                call.respond(HttpStatusCode.OK, "Change request updated successfully")
            }

            delete("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid change request ID")
                    return@delete
                }
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString() ?: error("role darf nicht null sein")
                if (role != "admin") {
                    call.respond(HttpStatusCode.Forbidden, "Admin access required")
                    return@delete
                }
                changeRequestDao.deleteChangeRequest(id)
                call.respond(HttpStatusCode.OK, "Change request deleted successfully")
            }

        }
    }
}