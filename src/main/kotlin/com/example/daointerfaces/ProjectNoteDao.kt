package com.example.daointerfaces

import com.example.datamodels.ProjectNote

interface ProjectNoteDao{
    suspend fun getAllProjectNotes(): List<ProjectNote>
    suspend fun getProjectNotes(projectId: Int): List<ProjectNote>
    suspend fun addProjectNote(projectId: Int, note: String, date: String): ProjectNote?
    suspend fun updateProjectNote(noteId: Int, note: String): Boolean
    suspend fun deleteProjectNote(noteId: Int): Boolean
}