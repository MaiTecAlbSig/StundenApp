
package com.example.Routes
import com.example.Customer
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import com.example.tables.Customers
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

 fun Route.customerRoutes() {
    route("/customers") {
        // Kunde erstellen
        post {
            val params = call.receiveParameters()
            val name = params["name"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Name required")
            val address = params["address"]
            val email = params["contact_email"]
            val phone = params["phone_number"]

            transaction {
                Customers.insert {
                    it[Customers.name] = name
                    it[Customers.address] = address
                    it[Customers.contact_email] = email
                    it[Customers.phone_number] = phone
                }
            }
            call.respond(HttpStatusCode.Created, "Customer created successfully")
        }

        // Alle Kunden abrufen
        get {
            val customers = transaction {
                Customers.selectAll().map {
                    Customer(
                        id = it[Customers.id],
                        name = it[Customers.name],
                        address = it[Customers.address],
                        contactEmail = it[Customers.contact_email],
                        phoneNumber = it[Customers.phone_number]
                    )
                }
            }
            call.respond(customers)
        }

        // Kunde nach ID abrufen
        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid customer ID")
                return@get
            }
            val customer = transaction {
                Customers.select { Customers.id eq id }.map {
                    Customer(
                        id = it[Customers.id],
                        name = it[Customers.name],
                        address = it[Customers.address],
                        contactEmail = it[Customers.contact_email],
                        phoneNumber = it[Customers.phone_number]
                    )
                }.singleOrNull()
            }
            if (customer == null) {
                call.respond(HttpStatusCode.NotFound, "Customer not found")
            } else {
                call.respond(customer)
            }
        }

        // Kunde aktualisieren
        put("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid customer ID")
                return@put
            }
            val params = call.receiveParameters()
            transaction {
                Customers.update({ Customers.id eq id }) {
                    params["name"]?.let { name -> it[Customers.name] = name }
                    params["address"]?.let { address -> it[Customers.address] = address }
                    params["contact_email"]?.let { email -> it[Customers.contact_email] = email }
                    params["phone_number"]?.let { phone -> it[Customers.phone_number] = phone }
                }
            }
            call.respond(HttpStatusCode.OK, "Customer updated successfully")
        }

        // Kunde löschen
        delete("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid customer ID")
                return@delete
            }

            transaction {
                Customers.deleteWhere { Customers.id eq id}
            }
            call.respond(HttpStatusCode.OK, "Customer deleted successfully")
        }
    }
}
