package com.example.tables

import com.sun.org.apache.xalan.internal.lib.ExsltDatetime.date
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import java.time.LocalDate

object Projects : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)
    val description = text("description").nullable()
    val customer_id = integer("customer_id").references(Customers.id).nullable()
    val start_date = varchar("start_date", 30).nullable()
    val end_date = varchar("end_date", 30).nullable()

    override val primaryKey = PrimaryKey(id)
}

