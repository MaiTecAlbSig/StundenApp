package com.example.datamodels

import kotlinx.serialization.Serializable

@Serializable
data class ChangeRequest(
    val id: Int,
    val employeeId: Int,
    val projectId: Int,
    val requestType: String,
    val requestDetails: String,
    val status: String,     // Z. B. "Pending", "Approved", "Rejected"
    val createdAt: String,   // Alternativ Date oder LocalDateTime
    val reviewedAt: String?
)
