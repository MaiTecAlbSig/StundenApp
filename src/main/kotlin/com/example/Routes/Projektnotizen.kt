// ProjectNotesRoutes.kt
package com.example.Routes

import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import com.example.tables.ProjectNotes

// Exposed-Funktionen und Operatoren importieren
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

fun Route.projectNotesRoutes() {
    route("/projects/{project_id}/notes") {
        // Notiz zu einem Projekt hinzufügen
        post {
            val projectId = call.parameters["project_id"]?.toIntOrNull()
            if (projectId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid project ID")
                return@post
            }

            val params = call.receiveParameters()
            val note = params["note"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Note content required")
            val filePath = params["file_path"]

            transaction {
                ProjectNotes.insert {
                    it[ProjectNotes.project_id] = projectId
                    it[ProjectNotes.note] = note
                    it[ProjectNotes.file_path] = filePath
                }
            }
            call.respond(HttpStatusCode.Created, "Note added successfully")
        }

        // Alle Notizen eines Projekts abrufen
        get {
            val projectId = call.parameters["project_id"]?.toIntOrNull()
            if (projectId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid project ID")
                return@get
            }

            val notes = transaction {
                ProjectNotes.select { ProjectNotes.project_id eq projectId }.map {
                    mapOf(
                        "id" to it[ProjectNotes.id],
                        "note" to it[ProjectNotes.note],
                        "file_path" to it[ProjectNotes.file_path],
                        "created_at" to it[ProjectNotes.created_at]
                    )
                }
            }
            call.respond(notes)
        }

        // Eine spezifische Notiz abrufen
        get("{note_id}") {
            val projectId = call.parameters["project_id"]?.toIntOrNull()
            val noteId = call.parameters["note_id"]?.toIntOrNull()

            if (projectId == null || noteId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid project or note ID")
                return@get
            }

            val note = transaction {
                ProjectNotes.select { (ProjectNotes.project_id eq projectId) and (ProjectNotes.id eq noteId) }
                    .map {
                        mapOf(
                            "id" to it[ProjectNotes.id],
                            "note" to it[ProjectNotes.note],
                            "file_path" to it[ProjectNotes.file_path],
                            "created_at" to it[ProjectNotes.created_at]
                        )
                    }
                    .singleOrNull()
            }

            if (note == null) {
                call.respond(HttpStatusCode.NotFound, "Note not found")
            } else {
                call.respond(note)
            }
        }

        // Notiz aktualisieren
        put("{note_id}") {
            val projectId = call.parameters["project_id"]?.toIntOrNull()
            val noteId = call.parameters["note_id"]?.toIntOrNull()

            if (projectId == null || noteId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid project or note ID")
                return@put
            }

            val params = call.receiveParameters()
            transaction {
                ProjectNotes.update({ (ProjectNotes.project_id eq projectId) and (ProjectNotes.id eq noteId) }) {
                    params["note"]?.let { note -> it[ProjectNotes.note] = note }
                    params["file_path"]?.let { filePath -> it[ProjectNotes.file_path] = filePath }
                }
            }
            call.respond(HttpStatusCode.OK, "Note updated successfully")
        }

        // Notiz löschen
        delete("{note_id}") {
            val projectId = call.parameters["project_id"]?.toIntOrNull()
            val noteId = call.parameters["note_id"]?.toIntOrNull()

            if (projectId == null || noteId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid project or note ID")
                return@delete
            }

            transaction {
                ProjectNotes.deleteWhere { (ProjectNotes.project_id eq projectId) and (ProjectNotes.id eq noteId) }
            }
            call.respond(HttpStatusCode.OK, "Note deleted successfully")
        }
    }
}
