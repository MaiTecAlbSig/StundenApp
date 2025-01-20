package com.example.routes

import com.example.daoImplemenations.ProjectAssignmentDaoImpl
import com.example.daointerfaces.ProjectDao
import com.example.datamodels.ProjectAssignment
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val formatter = DateTimeFormatter.ISO_DATE_TIME

fun LocalDateTime.toTimestampString(): String = this.format(formatter)

fun String.toLocalDateTime(): LocalDateTime = LocalDateTime.parse(this, formatter)

fun Route.projectRoutes(projectDao: ProjectDao) {
    authenticate("auth-jwt") {
        route("/project") {
            post {
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString() ?: error("role darf nicht null sein")
                if (role != "admin") {
                    call.respond(HttpStatusCode.Forbidden, "Admin access required")
                    return@post
                }
                val params = call.receiveParameters()
                val name =
                    params["name"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Project name required")
                val description = params["description"]
                val customerId = params["customer_id"]?.toIntOrNull()
                val startDate = params["start_date"]
                val endDate = params["end_date"]

                val artikel = projectDao.createProject(name, description, customerId, startDate, endDate)

                call.respond(HttpStatusCode.Created, "Project created successfully")
            }

            get {
                call.respond(HttpStatusCode.OK, projectDao.getAll())
            }

            get("/employee/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid employee ID")
                    return@get
                }
                val projectAssigmentDao = ProjectAssignmentDaoImpl()
                val projectIdList = projectAssigmentDao.getEmployeeAssignmentIds(id)
                val projectList = projectDao.getByEmployeeId(projectIdList)
                if (projectList == null) {
                    call.respond(HttpStatusCode.NotFound, "No Projects found")
                } else {
                    call.respond(projectList)
                }

            }
            get("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid project ID")
                    return@get
                }
                val project = projectDao.getById(id)
                if (project == null) {
                    call.respond(HttpStatusCode.NotFound, "Employee not found")
                } else {
                    call.respond(project)
                }
            }

            put("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid employee ID")
                    return@put
                }
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString() ?: error("role darf nicht null sein")
                if (role != "admin") {
                    call.respond(HttpStatusCode.Forbidden, "Admin access required")
                    return@put
                }
                val params = call.receiveParameters()
                val article = projectDao.updateProject(
                    id,
                    params["name"],
                    params["description"],
                    params["customer_id"]?.toIntOrNull(),
                    params["start_date"],
                    params["end_date"]
                )

                call.respond(HttpStatusCode.OK, "Project updated successfully")
            }

            delete("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Project ID")
                    return@delete
                }
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString() ?: error("role darf nicht null sein")
                if (role != "admin") {
                    call.respond(HttpStatusCode.Forbidden, "Admin access required")
                    return@delete
                }
                projectDao.deleteProject(id)
                call.respond(HttpStatusCode.OK, "Project deleted successfully")
            }
        }
    }
}