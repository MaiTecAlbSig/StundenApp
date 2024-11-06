package com.example.daoImplemenations

import com.example.DatabaseConfig.dbQuery
import com.example.daointerfaces.ProjectDao
import com.example.datamodels.Project
import com.example.tables.Projects
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq


class ProjectDaoImpl : ProjectDao {
    override suspend fun getAll(): List<Project> = dbQuery {
       Projects.selectAll().map(::resultRowToProjects)
    }

    override suspend fun getByEmployeeId(projectIds: List<Int>): List<Project> = dbQuery {
        Projects.select { Projects.id inList projectIds }.map(::resultRowToProjects)
    }

    override suspend fun getById(id: Int): Project? = dbQuery {
        Projects.select { Projects.id eq id}.map(::resultRowToProjects).singleOrNull()
    }

    override suspend fun createProject(name: String, description: String?, customerId: Int?, startDate: String?, endDate: String?): Project? = dbQuery {
        val insertStatement = Projects.insert {
            it[Projects.name] = name
            it[Projects.description] = description
            it[Projects.customer_id] = customerId
            it[Projects.start_date] = startDate
            it[Projects.end_date] = endDate
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToProjects)
    }

    override suspend fun updateProject(id: Int, name: String?, description: String?, customerId: Int?, startDate: String?, endDate: String?): Boolean = dbQuery {
        Projects.update({Projects.id eq id}) { row ->
            if (name != null){
                row[Projects.name] = name
            }
            if (description != null){
                row[Projects.description] = description
            }
            if (customerId != null){
                row[Projects.customer_id] = customerId
            }
            if (startDate != null){
                row[Projects.start_date] = startDate
            }
            if (endDate != null){
                row[Projects.end_date] = endDate
            }
        } > 0
    }



    override suspend fun deleteProject(id: Int): Boolean = dbQuery {
        Projects.deleteWhere { Projects.id eq id } > 0
    }


    private fun resultRowToProjects(row : ResultRow) = Project(
        id = row[Projects.id],
        name = row[Projects.name],
        description = row[Projects.description],
        customerId = row[Projects.customer_id],
        startDate = row[Projects.start_date],
        endDate = row[Projects.end_date]
    )
}