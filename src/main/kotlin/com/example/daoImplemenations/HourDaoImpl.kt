package com.example.daoImplemenations

import com.example.DatabaseConfig.dbQuery
import com.example.daointerfaces.HourDao
import com.example.datamodels.WorkHours
import com.example.tables.Hours
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class HourDaoImpl: HourDao {
    override suspend fun getAllHours(): List<WorkHours> = dbQuery {
        Hours.selectAll().map(::resultRowToWorkHours)
    }

    override suspend fun getHourByProject(id: Int): List<WorkHours> = dbQuery {
        Hours.select { Hours.project_id eq id }.map(::resultRowToWorkHours)
    }

    override suspend fun getHourByEmployee(id: Int): List<WorkHours> = dbQuery {
        Hours.select { Hours.employee_id eq id }.map(::resultRowToWorkHours)
    }

    override suspend fun getHourById(id: Int): WorkHours? = dbQuery{
        Hours.select { Hours.id eq id }.map(::resultRowToWorkHours).singleOrNull()
    }

    override suspend fun getHourByDay(day: String): List<WorkHours> = dbQuery {
        Hours.select { Hours.date eq day }.map(::resultRowToWorkHours)
    }

    override suspend fun addHour(projectId: Int, employeeId: Int, hours: Double, day: String, description: String
    ): WorkHours? = dbQuery {
        val insertStatement = Hours.insert {
            it[Hours.project_id] = projectId
            it[Hours.employee_id] = employeeId
            it[Hours.hours] = hours.toBigDecimal()
            it[Hours.date] = day
            it[Hours.description] = description
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToWorkHours)
    }

    override suspend fun updateHour(id: Int, hours: Double?, day: String?, description: String?): Boolean = dbQuery{
        Hours.update({Hours.id eq id}){
            if(hours != null) {
                it[Hours.hours] = hours.toBigDecimal()
            }
            if(day != null) {
                it[Hours.date] = day
            }
            if(description != null) {
                it[Hours.description] = description
            }
        } >0
    }

    override suspend fun deleteHour(id: Int): Boolean = dbQuery{
        Hours.deleteWhere { Hours.id eq id } > 0
    }

    private fun resultRowToWorkHours(row: ResultRow) = WorkHours(
        id = row[Hours.id],
        projectId = row[Hours.project_id],
        employeeId = row[Hours.employee_id],
        hours = row[Hours.hours].toDouble(),
        date = row[Hours.date],
        description = row[Hours.description]

    )
}