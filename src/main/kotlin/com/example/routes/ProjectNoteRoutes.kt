package com.example.routes

import com.example.daointerfaces.ProjectNoteDao
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun Route.projectNoteRoutes(projectNoteDao: ProjectNoteDao) {
    authenticate("auth-jwt") {
        route("/project-note") {
            post {
                val params = call.receiveParameters()
                val project_id =
                    params["project_id"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Project ID required")
                val note = params["note"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Note required")
                val currentDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                val article = projectNoteDao.addProjectNote(project_id.toInt(), note, currentDate)

                call.respond(HttpStatusCode.Created, "Project note created successfully")
            }
            get {
                call.respond(HttpStatusCode.OK, projectNoteDao.getAllProjectNotes())
            }
            get("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid project ID")
                    return@get
                }
                val projectNotes = projectNoteDao.getProjectNotes(id)
                if (projectNotes == null) {
                    call.respond(HttpStatusCode.NotFound, "No project notes found")
                } else {
                    call.respond(projectNotes)
                }
            }

            put("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid project ID")
                    return@put
                }
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString() ?: error("role darf nicht null sein")
                if (role != "admin") {
                    call.respond(HttpStatusCode.Forbidden, "Admin access required")
                    return@put
                }
                val params = call.receiveParameters()
                val article = projectNoteDao.updateProjectNote(id, params["note"].toString())

                call.respond(HttpStatusCode.OK, "Project note updated successfully")
            }

            delete("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid note ID")
                    return@delete
                }
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString() ?: error("role darf nicht null sein")
                if (role != "admin") {
                    call.respond(HttpStatusCode.Forbidden, "Admin access required")
                    return@delete
                }
                projectNoteDao.deleteProjectNote(id)
                call.respond(HttpStatusCode.OK, "Project note deleted successfully")
            }
        }
    }
}