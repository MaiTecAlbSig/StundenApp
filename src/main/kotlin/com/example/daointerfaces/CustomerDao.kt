package com.example.daointerfaces

import com.example.datamodels.Customer

interface CustomerDao {

    suspend fun allCustomers(): List<Customer>
    suspend fun getCustomer(id: Int): Customer?
    suspend fun createCustomer(name: String, address: String? = null,contactEmail: String? = null,phoneNumber: String? = null): Customer?
    suspend fun updateCustomer(id: Int, name: String? = null, address: String? = null,contactEmail: String? = null,phoneNumber: String? = null): Boolean
    suspend fun deleteCustomer(id: Int): Boolean
}
