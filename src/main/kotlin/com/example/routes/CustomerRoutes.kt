package com.example.routes

import com.example.daointerfaces.CustomerDao
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.customerRoutes(customerDao: CustomerDao) {
    authenticate("auth-jwt") {
        route("/customer") {
            // Kunde erstellen
            post {
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString() ?: error("role darf nicht null sein")
                if (role != "admin") {
                    call.respond(HttpStatusCode.Forbidden, "Admin access required")
                    return@post
                }
                val params = call.receiveParameters()
                val name = params["name"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Name required")
                val address = params["address"]
                val email = params["contact_email"]
                val phone = params["phone_number"]
                val article = customerDao.createCustomer(name, address, email, phone)

                call.respond(HttpStatusCode.Created, "Customer created successfully")
            }

            get {
                call.respond(HttpStatusCode.OK, customerDao.allCustomers())
            }

            get("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid customer ID")
                    return@get
                }
                val customer = customerDao.getCustomer(id)
                if (customer == null) {
                    call.respond(HttpStatusCode.NotFound, "Customer not found")
                } else {
                    call.respond(customer)
                }
            }
            put("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid customer ID")
                    return@put
                }
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString() ?: error("role darf nicht null sein")
                if (role != "admin") {
                    call.respond(HttpStatusCode.Forbidden, "Admin access required")
                    return@put
                }
                val params = call.receiveParameters()
                val article = customerDao.updateCustomer(
                    id,
                    params["name"],
                    params["address"],
                    params["contact_email"],
                    params["phone_number"]
                )
                call.respond(HttpStatusCode.OK, "Customer updated successfully")
            }
            delete("{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid employee ID")
                    return@delete
                }
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString() ?: error("role darf nicht null sein")
                if (role != "admin") {
                    call.respond(HttpStatusCode.Forbidden, "Admin access required")
                    return@delete
                }
                customerDao.deleteCustomer(id)
                call.respond(HttpStatusCode.OK, "Customer deleted successfully")
            }

        }
    }
}