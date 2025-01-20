package com.example.tables

import org.jetbrains.exposed.sql.Table

object Customers : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)
    val address = text("address").nullable()
    val contact_email = varchar("contact_email", 100).nullable()
    val phone_number = varchar("phone_number", 20).nullable()

    override val primaryKey = PrimaryKey(id)
}



