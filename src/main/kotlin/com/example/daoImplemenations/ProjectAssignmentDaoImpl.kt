package com.example.daoImplemenations

import com.example.DatabaseConfig.dbQuery
import com.example.daointerfaces.ProjectAssigmentDao
import com.example.datamodels.ProjectAssignment
import com.example.tables.ProjectAssignments
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq


class ProjectAssignmentDaoImpl : ProjectAssigmentDao {
    override suspend fun getAllAssignment(): List<ProjectAssignment> = dbQuery {
        ProjectAssignments.selectAll().map(::resultRowToProjectAssignment)
    }

    override suspend fun getAllAssignmentsByProject(projectId: Int): List<ProjectAssignment> = dbQuery {
        ProjectAssignments.select{ProjectAssignments.project_id eq projectId}.map(::resultRowToProjectAssignment)
    }

    override suspend fun getAllAssignmentsByEmployee(employeeId: Int): List<ProjectAssignment> = dbQuery {
        ProjectAssignments.select{ProjectAssignments.employee_id eq employeeId}.map(::resultRowToProjectAssignment)
    }

    override suspend fun createAssignment(projectId: Int, employeeId: Int, role: String?): ProjectAssignment? = dbQuery {
        val insertStatement = ProjectAssignments.insert {
            it[ProjectAssignments.project_id] = projectId
            it[ProjectAssignments.employee_id] = employeeId
            it[ProjectAssignments.role] = role ?: "Employee"
        }
        insertStatement.resultedValues?.singleOrNull()?.let (::resultRowToProjectAssignment)
    }

    override suspend fun deleteAssignment(projectId: Int, employeeId: Int): Boolean = dbQuery {
        ProjectAssignments.deleteWhere { (ProjectAssignments.project_id eq projectId) and (ProjectAssignments.employee_id eq employeeId) } > 0
    }

    override suspend fun getEmployeeAssignmentIds(employeeId: Int): List<Int> = dbQuery {
        ProjectAssignments.select { ProjectAssignments.employee_id eq employeeId }.map{ it[ProjectAssignments.project_id]}
    }

    override suspend fun changeAssignment(projectId: Int, employeeId: Int, role: String): Boolean = dbQuery{
        ProjectAssignments.update({(ProjectAssignments.project_id eq projectId) and (ProjectAssignments.employee_id eq employeeId)}){
            it[ProjectAssignments.role] = role
        } > 0
    }

    private fun resultRowToProjectAssignment(row: ResultRow) = ProjectAssignment (
        projectId = row[ProjectAssignments.project_id],
        employeeId = row[ProjectAssignments.employee_id],
        role = row[ProjectAssignments.role]
    )
}