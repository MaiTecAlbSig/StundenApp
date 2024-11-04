package com.example.daointerfaces

import com.example.Employee

interface EmployeeDao {
    suspend fun add(employee: Employee)
    suspend fun all(): List<Employee>
    suspend fun remove(employee: Employee): Boolean

}