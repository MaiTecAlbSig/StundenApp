package com.example

// DatabaseConfig.kt

import org.jetbrains.exposed.sql.Database

object DatabaseConfig {
    fun connect() {
        val dbUrl = System.getenv("DATABASE_URL") ?: throw IllegalStateException("Database URL is missing")
        Database.connect(
            dbUrl,
            driver = "org.postgresql.Driver",
            user = "mobsyscloud_postgresql_1_user",
            password = "fmo1lZdQJzfKZlRWqUJmMoebgjcPQUUL"
        )
        println("Datenbankverbindung hergestellt") // Testausgabe

    }
}
