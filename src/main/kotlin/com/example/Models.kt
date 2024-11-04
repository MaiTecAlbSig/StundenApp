package com.example

import kotlinx.serialization.Serializable

@Serializable
data class Customer(
    val id: Int,
    val name: String,
    val address: String?,
    val contactEmail: String?,
    val phoneNumber: String?
)

@Serializable
data class Employee(
    val id: Int,
    val name: String,
    val position: String,
    val email: String?,
    val phoneNumber: String?
)

@Serializable
data class Project(
    val id: Int,
    val name: String,
    val description: String?,
    val startDate: String?,   // Alternativ kann ein Date-Typ verwendet werden, je nach Bedarf
    val endDate: String?
)

@Serializable
data class ProjectAssignment(
    val projectId: Int,
    val employeeId: Int,
    val role: String
)

@Serializable
data class ProjectNote(
    val id: Int,
    val projectId: Int,
    val content: String,
    val timestamp: String    // Alternativ Date oder LocalDateTime
)

@Serializable
data class WorkHours(
    val id: Int,
    val employeeId: Int,
    val projectId: Int,
    val hours: Double,
    val date: String         // Alternativ Date oder LocalDate
)

@Serializable
data class ChangeRequest(
    val id: Int,
    val employeeId: Int,
    val projectId: Int,
    val requestedChange: String,
    val status: String,     // Z. B. "Pending", "Approved", "Rejected"
    val timestamp: String   // Alternativ Date oder LocalDateTime
)
