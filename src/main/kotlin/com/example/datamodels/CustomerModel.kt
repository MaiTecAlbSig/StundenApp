package com.example.datamodels

import kotlinx.serialization.Serializable

@Serializable
data class Customer(
    val id: Int,
    val name: String,
    val address: String?,
    val contactEmail: String?,
    val phoneNumber: String?
)
