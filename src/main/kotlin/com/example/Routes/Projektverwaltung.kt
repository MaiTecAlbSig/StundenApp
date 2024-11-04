package com.example.Routes

import com.example.tables.ProjectAssignments
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import com.example.tables.Projects // Tabelle für Projekte
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import kotlinx.serialization.Serializable

// Exposed-Funktionen und Operatoren importieren
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val formatter = DateTimeFormatter.ISO_DATE_TIME

fun LocalDateTime.toTimestampString(): String = this.format(formatter)

fun String.toLocalDateTime(): LocalDateTime = LocalDateTime.parse(this, formatter)

fun Route.projectRoutes() {
    authenticate("auth-jwt") {
    route("/projects") {
        // Projekt erstellen
        post {
            val params = call.receiveParameters()
            val name = params["name"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Project name required")
            val description = params["description"]
            val customerId = params["customer_id"]?.toIntOrNull()
            val startDate = params["start_date"]?.let { it.toLocalDateTime().toTimestampString() }
            val endDate = params["end_date"]?.let { it.toLocalDateTime().toTimestampString() }

            transaction {
                Projects.insert {
                    it[Projects.name] = name
                    it[Projects.description] = description
                    it[Projects.customer_id] = customerId
                    it[Projects.start_date] = startDate
                    it[Projects.end_date] = endDate
                }
            }
            call.respond(HttpStatusCode.Created, "Project created successfully")
        }

        // Alle Projekte abrufen
        get {
            // Vorhandenen Code zur Bearbeitung der GET-Anfrage
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.payload?.getClaim("userId")?.asInt()
            val nonNullUserId = userId ?: error("userId darf nicht null sein")
            val role = principal?.payload?.getClaim("role")?.asString()

            val projects = transaction {
                if (role == "admin") {
                    // Admin: Alle Projekte abrufen
                    Projects.selectAll().map {
                        ProjectResponse(
                            id = it[Projects.id],
                            name = it[Projects.name],
                            description = it[Projects.description],
                            customer_id = it[Projects.customer_id],
                            start_date = it[Projects.start_date],
                            end_date = it[Projects.end_date]
                        )
                    }
                } else if (role == "user") {
                    // Benutzer: Nur verknüpfte Projekte abrufen
                    ProjectAssignments
                        .select { ProjectAssignments.employee_id eq nonNullUserId }
                        .map { assignment ->
                            val projectId = assignment[ProjectAssignments.project_id]
                            Projects
                                .select { Projects.id eq projectId }
                                .map { project ->
                                    ProjectResponse(
                                        id = project[Projects.id],
                                        name = project[Projects.name],
                                        description = project[Projects.description],
                                        customer_id = project[Projects.customer_id],
                                        start_date = project[Projects.start_date],
                                        end_date = project[Projects.end_date]
                                    )
                                }
                        }.flatten()
                } else {
                    emptyList()
                }
            }
            call.respond(projects)
        }


        // Projekt nach ID abrufen
        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid project ID")
                return@get
            }
            val project = transaction {
                Projects.select { Projects.id eq id }.map {
                    mapOf(
                        "id" to it[Projects.id],
                        "name" to it[Projects.name],
                        "description" to it[Projects.description],
                        "customer_id" to it[Projects.customer_id],
                        "start_date" to it[Projects.start_date],
                        "end_date" to it[Projects.end_date]
                    )
                }.singleOrNull()
            }
            if (project == null) {
                call.respond(HttpStatusCode.NotFound, "Project not found")
            } else {
                call.respond(project)
            }
        }

        // Projekt aktualisieren
        put("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid project ID")
                return@put
            }
            val params = call.receiveParameters()
            transaction {
                Projects.update({ Projects.id eq id }) {
                    params["name"]?.let { name -> it[Projects.name] = name }
                    params["description"]?.let { description -> it[Projects.description] = description }
                    params["customer_id"]?.toIntOrNull()?.let { customerId -> it[Projects.customer_id] = customerId }
                    params["start_date"]?.let { startDate -> it[Projects.start_date] = startDate }
                    params["end_date"]?.let { endDate -> it[Projects.end_date] = endDate }
                }
            }
            call.respond(HttpStatusCode.OK, "Project updated successfully")
        }

        // Projekt löschen
        delete("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid project ID")
                return@delete
            }
            transaction {
                Projects.deleteWhere { Projects.id eq id }
            }
            call.respond(HttpStatusCode.OK, "Project deleted successfully")
        }
    }
    }
}

@Serializable
data class ProjectResponse(
    val id: Int,
    val name: String,
    val description: String?,
    val customer_id: Int?,
    val start_date: String?,
    val end_date: String?
)
