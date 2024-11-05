package com.example.daoImplemenations

import com.example.DatabaseConfig.dbQuery
import com.example.daointerfaces.CustomerDao
import com.example.datamodels.Customer
import com.example.tables.Customers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class CustomerDaoImpl : CustomerDao {

    override suspend fun allCustomers(): List<Customer> = dbQuery {
        Customers.selectAll().map(::resultRowToCustomer)
    }

    override suspend fun getCustomer(id: Int): Customer?= dbQuery {
        Customers.select { Customers.id eq id }.map(::resultRowToCustomer).singleOrNull()
    }

    override suspend fun createCustomer(name: String, address: String?,contactEmail: String?,phoneNumber: String?): Customer? = dbQuery {
        val insertStatement = Customers.insert {
            it[Customers.name] = name
            it[Customers.address] = address
            it[Customers.contact_email] = contactEmail
            it[Customers.phone_number] = phoneNumber
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToCustomer)
    }

    override suspend fun updateCustomer(id: Int, name: String?, address: String?, contactEmail: String?, phoneNumber: String?): Boolean = dbQuery {
        Customers.update({ Customers.id eq id }) { row ->
            if (name != null) {
                row[Customers.name] = name
            }
            if (address != null) {
                row[Customers.address] = address
            }
            if (contactEmail != null) {
                row[Customers.contact_email] = contactEmail
            }
            if (phoneNumber != null) {
                row[Customers.phone_number] = phoneNumber
            }
        } > 0
    }


    override suspend fun deleteCustomer(id: Int): Boolean = dbQuery {
        Customers.deleteWhere { Customers.id eq id } > 0
    }

    private fun resultRowToCustomer(row : ResultRow) = Customer(
        id = row[Customers.id],
        name = row[Customers.name],
        address = row[Customers.address],
        contactEmail = row[Customers.contact_email],
        phoneNumber = row[Customers.phone_number]
    )
}

