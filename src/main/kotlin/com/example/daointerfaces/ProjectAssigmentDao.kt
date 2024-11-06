package com.example.daointerfaces

import com.example.datamodels.ProjectAssignment

interface ProjectAssigmentDao{
    suspend fun getAllAssignment(): List<ProjectAssignment>
    suspend fun getAllAssignmentsByProject(projectId: Int): List<ProjectAssignment>
    suspend fun getAllAssignmentsByEmployee(employeeId: Int): List<ProjectAssignment>
    suspend fun createAssignment(projectId: Int, employeeId: Int, role: String?): ProjectAssignment?
    suspend fun deleteAssignment(projectId: Int, employeeId: Int): Boolean
    suspend fun getEmployeeAssignmentIds(employeeId: Int): List<Int>
    suspend fun changeAssignment(projectId: Int, employeeId: Int, role: String): Boolean

}