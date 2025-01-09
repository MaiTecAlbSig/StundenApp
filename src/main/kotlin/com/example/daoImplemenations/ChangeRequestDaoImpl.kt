package com.example.daoImplemenations

import com.example.DatabaseConfig.dbQuery
import com.example.daointerfaces.ChangeRequestDao
import com.example.datamodels.ChangeRequest
import com.example.tables.ChangeRequests
import com.example.tables.ProjectNotes
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class ChangeRequestDaoImpl: ChangeRequestDao {
    override suspend fun getChangeRequests(): List<ChangeRequest> = dbQuery{
        ChangeRequests.selectAll().map(::resultRowToChangeRequest)
    }

    override suspend fun getChangeRequestById(changeRequestId: Int): ChangeRequest? = dbQuery{
        ChangeRequests.select { ChangeRequests.id eq changeRequestId }.map(::resultRowToChangeRequest).singleOrNull()
    }

    override suspend fun getChangeRequestsByEmployeeId(employeeId: Int): List<ChangeRequest> = dbQuery{
        ChangeRequests.select { ChangeRequests.employee_id eq employeeId }.map(::resultRowToChangeRequest)
    }

    override suspend fun getChangeRequestsByProjectId(projectId: Int): List<ChangeRequest> = dbQuery{
        ChangeRequests.select { ChangeRequests.project_id eq projectId }.map(::resultRowToChangeRequest)
    }

    override suspend fun getChangeRequestsByStatus(status: String): List<ChangeRequest> = dbQuery{
        ChangeRequests.select { ChangeRequests.status eq status }.map(::resultRowToChangeRequest)
    }

    override suspend fun addChangeRequest(projectId: Int, employeeId: Int, requestType: String, requestDetails: String, date: String): ChangeRequest? = dbQuery {
        val insertStatement = ChangeRequests.insert {
            it[ChangeRequests.project_id] = projectId
            it[ChangeRequests.employee_id] = employeeId
            it[ChangeRequests.request_type] = requestType
            it[ChangeRequests.request_details] = requestDetails
            it[ChangeRequests.created_at] = date
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToChangeRequest)
    }

    override suspend fun updateChangeRequest(changeRequestId: Int, requestType: String?, requestDetails: String?, status: String?, reviewedAt: String): Boolean = dbQuery {
        ChangeRequests.update ({ ChangeRequests.id eq changeRequestId}){
            if (requestType != null) {
                it[ChangeRequests.request_type] = requestType
            }
            if (requestDetails != null) {
                it[ChangeRequests.request_details] = requestDetails
            }
            if (status != null) {
                it[ChangeRequests.status] = status
            }

            it[ChangeRequests.reviewed_at] = reviewedAt

        } > 0
    }

    override suspend fun deleteChangeRequest(changeRequestId: Int): Boolean = dbQuery {
        ChangeRequests.deleteWhere { ChangeRequests.id eq changeRequestId } > 0
    }

    private fun resultRowToChangeRequest(row: ResultRow) = ChangeRequest (
        id = row[ChangeRequests.id],
        projectId = row[ChangeRequests.project_id],
        employeeId = row[ChangeRequests.employee_id],
        requestType = row[ChangeRequests.request_type],
        requestDetails = row[ChangeRequests.request_details],
        status = row[ChangeRequests.status],
        createdAt = row[ChangeRequests.created_at],
        reviewedAt = row[ChangeRequests.reviewed_at]
    )
}