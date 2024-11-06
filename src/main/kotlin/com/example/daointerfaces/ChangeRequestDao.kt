package com.example.daointerfaces

import com.example.datamodels.ChangeRequest

interface ChangeRequestDao{
    suspend fun getChangeRequests(): List<ChangeRequest>
    suspend fun getChangeRequestById(changeRequestId: Int): ChangeRequest?
    suspend fun getChangeRequestsByEmployeeId(employeeId: Int): List<ChangeRequest>
    suspend fun getChangeRequestsByProjectId(projectId: Int): List<ChangeRequest>
    suspend fun getChangeRequestsByStatus(status: String): List<ChangeRequest>
    suspend fun addChangeRequest(projectId: Int, employeeId: Int, requestType: String, requestDetails: String, date: String): ChangeRequest?
    suspend fun updateChangeRequest(changeRequestId: Int, requestType: String?, requestDetails: String?, status: String?, reviewedAt: String): Boolean
    suspend fun deleteChangeRequest(changeRequestId: Int): Boolean
}