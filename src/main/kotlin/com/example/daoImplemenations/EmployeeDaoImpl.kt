package com.example.daoImplemenations

import com.example.DatabaseConfig.dbQuery
import com.example.daointerfaces.EmployeeDao
import com.example.datamodels.Employee
import com.example.tables.Employees
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class EmployeeDaoImpl : EmployeeDao {
    override suspend fun allEmployees(): List<Employee> = dbQuery {
        Employees.selectAll().map(::resultRowToEmployee)
    }

    override suspend fun getEmployee(id: Int): Employee? = dbQuery {
        Employees.select { Employees.id eq id }.map(::resultRowToEmployee).singleOrNull()
    }

    override suspend fun createEmployee(name: String, position: String, email: String, passwordHash: String, isAdmin: Boolean): Employee? = dbQuery {
        val insertStatement = Employees.insert {
            it[Employees.name] = name
            it[Employees.position] = position
            it[Employees.email] = email
            it[Employees.password_hash] = passwordHash
            it[Employees.isAdmin] = isAdmin
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToEmployee)
    }

    override suspend fun updateEmployee(id: Int, name: String?, position: String?, email: String?, isAdmin: Boolean?): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun updatePassword(email: String, oldPassword: String, newPassword: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteEmployee(id: Int): Boolean {
        TODO("Not yet implemented")
    }

    private fun resultRowToEmployee(row : ResultRow) = Employee(
        id = row[Employees.id],
        name = row[Employees.name],
        position = row[Employees.position],
        email = row[Employees.email],
        isAdmin = row[Employees.isAdmin],
        password_hash = row[Employees.password_hash]
    )
}