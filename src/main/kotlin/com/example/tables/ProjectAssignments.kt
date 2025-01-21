package com.example.tables

import org.jetbrains.exposed.sql.Table

object ProjectAssignments : Table() {
    val project_id = integer("project_id").references(Projects.id)
    val employee_id = integer("employee_id").references(Employees.id)
    val role = varchar("role", 50).nullable()

    override val primaryKey = PrimaryKey(project_id, employee_id)
}
