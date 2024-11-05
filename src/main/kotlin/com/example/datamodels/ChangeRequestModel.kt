package com.example.datamodels

import kotlinx.serialization.Serializable

@Serializable
data class ChangeRequest(
    val id: Int,
    val employeeId: Int,
    val projectId: Int,
    val requestedChange: String,
    val status: String,     // Z. B. "Pending", "Approved", "Rejected"
    val timestamp: String   // Alternativ Date oder LocalDateTime
)
