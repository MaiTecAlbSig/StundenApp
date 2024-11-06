package com.example.datamodels

import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val id: Int,
    val name: String,
    val description: String?,
    val customerId: Int?,
    val startDate: String?,   // Alternativ kann ein Date-Typ verwendet werden, je nach Bedarf
    val endDate: String?
)
