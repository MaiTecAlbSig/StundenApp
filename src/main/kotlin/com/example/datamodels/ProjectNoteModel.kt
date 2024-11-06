package com.example.datamodels

import kotlinx.serialization.Serializable

@Serializable
data class ProjectNote(
    val id: Int,
    val projectId: Int,
    val note: String,
    val timestamp: String    // Alternativ Date oder LocalDateTime
)
