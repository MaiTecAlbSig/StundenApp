package com.example.daointerfaces

import com.example.Customer

interface CustomerDao {

    suspend fun allCustomers(): List<Customer>
    suspend fun getCustomer(id: Int): Customer?
    suspend fun createCustomer(customer: Customer)
    suspend fun deleteCustomer(id: Int)
}