package com.example

// DatabaseConfig.kt

import org.jetbrains.exposed.sql.Database

object DatabaseConfig {
    fun connect() {
        println("Attempting to connect to the database...")
        val dbUrl = System.getenv("DATABASE_URL") ?: throw IllegalStateException("Database URL is missing")
        val dbUser = System.getenv("DATABASE_USER") ?: throw IllegalStateException("Database user is missing")
        val dbPassword = System.getenv("DATABASE_PASSWORD") ?: throw IllegalStateException("Database password is missing")
        Database.connect(
            dbUrl,
            driver = "org.postgresql.Driver",
            user = dbUser,
            password = dbPassword
        )
        println("Datenbankverbindung hergestellt") // Testausgabe

    }
}
