package com.example.datamodels

import kotlinx.serialization.Serializable

@Serializable
data class Employee(
    val id: Int,
    val name: String,
    val position: String?,
    val email: String,
    val password_hash: String,
    val isAdmin: Boolean
)
