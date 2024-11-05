package com.example.datamodels

import kotlinx.serialization.Serializable

@Serializable
data class WorkHours(
    val id: Int,
    val employeeId: Int,
    val projectId: Int,
    val hours: Double,
    val date: String         // Alternativ Date oder LocalDate
)
