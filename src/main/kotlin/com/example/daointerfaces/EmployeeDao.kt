package com.example.daointerfaces

import com.example.datamodels.Employee

interface EmployeeDao {
    suspend fun allEmployees(): List<Employee>
    suspend fun getEmployee(id: Int): Employee?
    suspend fun createEmployee(name: String, position: String,email: String, passwordHash: String, isAdmin: Boolean ): Employee?
    suspend fun updateEmployee(id: Int, name: String? = null, position: String? = null,email: String? = null,isAdmin: Boolean? = null): Boolean
    suspend fun updatePassword(email: String, oldPassword: String, newPassword: String): Boolean
    suspend fun deleteEmployee(id: Int): Boolean
}