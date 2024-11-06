package com.example.routes

import com.example.daointerfaces.HourDao
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.hourRoutes(hourDao: HourDao) {
    route("/hours") {
        post {
            val params = call.receiveParameters()
            val projectId = params["project_Id"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Project ID required")
            val employeeId = params["employee_Id"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Employee ID required")
            val hour_worked = params["hours_worked"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Hour required")
            val date = params["date"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Day required")
            val description = params["description"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Description required")

            val article = hourDao.addHour(projectId.toInt(),employeeId.toInt(),hour_worked.toDouble(), date, description)

            call.respond(HttpStatusCode.Created, "Hour created successfully")
        }

        get {
            call.respond(HttpStatusCode.OK, hourDao.getAllHours())
        }

        get("{id}"){
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid Hour ID")
                return@get
            }
            val hour = hourDao.getHourById(id)
            if (hour == null) {
                call.respond(HttpStatusCode.NotFound, "Hour not found")
            } else {
                call.respond(hour)
            }
        }

        get("/employee/{id}"){
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid employee ID")
                return@get
            }
            val employeeHours = hourDao.getHourByEmployee(id)
            if (employeeHours == null) {
                call.respond(HttpStatusCode.NotFound, "Employee not found")
            } else {
                call.respond(employeeHours)
            }
        }

        get("/project/{id}"){
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid project ID")
                return@get
            }
            val projectHours = hourDao.getHourByProject(id)
            if (projectHours == null) {
                call.respond(HttpStatusCode.NotFound, "Project not found")
            } else {
                call.respond(projectHours)
            }
        }
        get("/day/{day}"){
            val day = call.parameters["day"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Day required")
            val projectHours = hourDao.getHourByDay(day)
            if (projectHours == null) {
                call.respond(HttpStatusCode.NotFound, "Day not found")
            } else {
                call.respond(projectHours)
            }
        }
        put("{id}"){
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid Hour ID")
                return@put
            }
            val params = call.receiveParameters()
            val article = hourDao.updateHour(
                id,
                params["hours_worked"]?.toDoubleOrNull(),
                params["date"],
                params["description"]
            )
            call.respond(HttpStatusCode.OK, "Hour updated successfully")
        }
        delete("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid Hour ID")
                return@delete
            }
            hourDao.deleteHour(id)
            call.respond(HttpStatusCode.OK, "Hour deleted successfully")
        }
    }
}