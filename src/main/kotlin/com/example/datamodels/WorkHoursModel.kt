package com.example.datamodels

import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class WorkHours(
    val id: Int,
    val employeeId: Int,
    val projectId: Int,
    val hours: Double,
    val date: String, // Alternativ Date oder LocalDate
    val description: String?
)
