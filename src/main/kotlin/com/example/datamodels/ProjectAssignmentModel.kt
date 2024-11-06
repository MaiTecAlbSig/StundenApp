package com.example.datamodels

import kotlinx.serialization.Serializable

@Serializable
data class ProjectAssignment(
    val projectId: Int,
    val employeeId: Int,
    val role: String?
)
