package com.example.tables

import org.jetbrains.exposed.sql.Table

object ProjectNotes : Table() {
    val id = integer("id").autoIncrement()
    val project_id = integer("project_id").references(Projects.id) // Verweis auf Projekte
    val note = text("note")
    val file_path = varchar("file_path", 255).nullable() // Pfad zur Datei (optional)
    val created_at = varchar("created_at", 19).default("CURRENT_TIMESTAMP") // Zeitstempel als String speichern

    override val primaryKey = PrimaryKey(id)
}
