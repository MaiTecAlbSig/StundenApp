package com.example.tables


import org.jetbrains.exposed.sql.Table

object Hours : Table() {
    val id = integer("id").autoIncrement()
    val project_id = integer("project_id").references(Projects.id) // Verweis auf Projekte
    val employee_id = integer("employee_id").references(Employees.id) // Verweis auf Mitarbeiter
    val hours = decimal("hours", 5, 2) // Arbeitsstunden
    val date = varchar("date", 10) // Datum als String speichern (Format: yyyy-MM-dd)
    val description = text("description").nullable() // Beschreibung der Tätigkeit (optional)

    override val primaryKey = PrimaryKey(id)
}
