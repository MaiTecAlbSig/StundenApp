package com.example

// DatabaseConfig.kt

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

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
    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
