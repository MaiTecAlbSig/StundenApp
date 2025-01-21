package com.example.tables

import org.jetbrains.exposed.sql.Table

object ChangeRequests : Table() {
    val id = integer("id").autoIncrement()
    val project_id = integer("project_id").references(Projects.id)
    val employee_id = integer("employee_id").references(Employees.id)
    val request_type = varchar("request_type", 50)
    val request_details = text("request_details")
    val status = varchar("status", 20).default("Pending")
    val created_at = varchar("created_at", 19)
    val reviewed_at = varchar("reviewed_at", 19).nullable()

    override val primaryKey = PrimaryKey(id)
}
