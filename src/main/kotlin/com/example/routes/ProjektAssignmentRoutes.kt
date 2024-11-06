package com.example.routes

import com.example.daointerfaces.ProjectAssigmentDao
import com.example.tables.ProjectAssignments
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

fun Route.projectAssignmentRoutes(projectAssigmentDao: ProjectAssigmentDao) {
    route("/project-assignment") {
        get {
            call.respond(HttpStatusCode.OK, projectAssigmentDao.getAllAssignment())
        }
        get("/project/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid project ID")
                return@get
            }
            val projectAssignments = projectAssigmentDao.getAllAssignmentsByProject(id)
            if (projectAssignments == null){
                call.respond(HttpStatusCode.NotFound, "No project assignments found")
            } else {
                call.respond(projectAssignments)
            }
        }
        get("/employee/{id}"){
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid employee ID")
                return@get
            }
            val employeeAssignments = projectAssigmentDao.getAllAssignmentsByEmployee(id)
            if (employeeAssignments == null){
                call.respond(HttpStatusCode.NotFound, "No employee assignments found")
            } else {
                call.respond(employeeAssignments)
            }
        }

        get("/employee-to-id/{id}"){
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid employee ID")
                return@get
            }
            val employeeAssignmentIds = projectAssigmentDao.getEmployeeAssignmentIds(id)
            if (employeeAssignmentIds == null){
                call.respond(HttpStatusCode.NotFound, "No employee assignments found")
            } else {
                call.respond(employeeAssignmentIds)
            }
        }

        post {
            val params = call.receiveParameters()
            val project_id = params["project_id"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Project ID required")
            val employee_id = params["employee_id"] ?: return@post call.respond(HttpStatusCode.BadRequest, "Employee ID required")
            val role = params["role"]

            val article = projectAssigmentDao.createAssignment(project_id.toInt(), employee_id.toInt(), role)

            call.respond(HttpStatusCode.Created, "Project assignment created successfully")
        }

        put("{project_id}/{employee_id}"){
            val projectId = call.parameters["project_id"]?.toIntOrNull()
            if (projectId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid project ID")
                return@put
            }
            val employeeId = call.parameters["employee_id"]?.toIntOrNull()
            if (employeeId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid employee ID")
                return@put
            }

            val params = call.receiveParameters()

            val role = params["role"]
            if (role == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid new role")
                return@put
            }
            val article = projectAssigmentDao.changeAssignment(projectId,employeeId,role)

            call.respond(HttpStatusCode.OK, "Project assignment updated successfully")
        }

        delete("{project_id}/{employee_id}") {
            val projectId = call.parameters["project_id"]?.toIntOrNull()
            if (projectId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid project ID")
                return@delete
            }
            val employeeId = call.parameters["employee_id"]?.toIntOrNull()
            if (employeeId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid employee ID")
                return@delete
            }
            projectAssigmentDao.deleteAssignment(projectId,employeeId)

            call.respond(HttpStatusCode.OK, "Project assignment deleted successfully")


        }
    }


}