package com.example.datamodels

import kotlinx.serialization.Serializable

@Serializable
data class ChangeRequest(
    val id: Int,
    val employeeId: Int,
    val projectId: Int,
    val requestType: String,
    val requestDetails: String,
    val status: String,
    val createdAt: String,
    val reviewedAt: String?
)
