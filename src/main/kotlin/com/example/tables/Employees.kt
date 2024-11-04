package com.example.tables

import org.jetbrains.exposed.sql.Table

object Employees : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)
    val position = varchar("position", 50).nullable()
    val email = varchar("email", 100).uniqueIndex()
    val password_hash = varchar("password_hash", 64) // Passwort als Hash speichern
    val isAdmin = bool("is_admin").default(false)

    override val primaryKey = PrimaryKey(id)
}
