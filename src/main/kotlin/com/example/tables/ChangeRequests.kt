package com.example.tables

import org.jetbrains.exposed.sql.Table

object ChangeRequests : Table() {
    val id = integer("id").autoIncrement()
    val project_id = integer("project_id").references(Projects.id) // Verweis auf Projekte
    val employee_id = integer("employee_id").references(Employees.id) // Verweis auf Mitarbeiter
    val request_type = varchar("request_type", 50) // Typ der Anfrage, z.B. "Hours Change"
    val request_details = text("request_details") // Details der angeforderten Änderung
    val status = varchar("status", 20).default("Pending") // Status: Pending, Approved, Rejected
    val created_at = varchar("created_at", 19).default("CURRENT_TIMESTAMP") // Erstellungsdatum als String
    val reviewed_at = varchar("reviewed_at", 19).nullable() // Zeitpunkt der Bearbeitung als String

    override val primaryKey = PrimaryKey(id)
}
