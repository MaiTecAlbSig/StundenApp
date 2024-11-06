package com.example.daoImplemenations

import com.example.DatabaseConfig.dbQuery
import com.example.daointerfaces.ProjectNoteDao
import com.example.datamodels.ProjectNote
import com.example.tables.ProjectNotes
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class ProjectNoteDaoImpl : ProjectNoteDao {
    override suspend fun getAllProjectNotes(): List<ProjectNote> = dbQuery{
        ProjectNotes.selectAll().map(::resultRowToProjectNote)
    }

    override suspend fun getProjectNotes(projectId: Int): List<ProjectNote> = dbQuery {
        ProjectNotes.select { ProjectNotes.project_id eq projectId }.map(::resultRowToProjectNote)
    }

    override suspend fun addProjectNote(projectId: Int, note: String, date: String): ProjectNote? = dbQuery {
        val insertStatement = ProjectNotes.insert {
            it[ProjectNotes.project_id] = projectId
            it[ProjectNotes.note] = note
            it[ProjectNotes.created_at] = date
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToProjectNote)
    }

    override suspend fun updateProjectNote(noteId: Int, note: String): Boolean = dbQuery{
        ProjectNotes.update({ProjectNotes.id eq noteId}){
            it[ProjectNotes.note] = note
        } > 0
    }

    override suspend fun deleteProjectNote(noteId: Int): Boolean = dbQuery{
        ProjectNotes.deleteWhere { ProjectNotes.id eq noteId } > 0
    }

    private fun resultRowToProjectNote(row: ResultRow) = ProjectNote (
        id = row[ProjectNotes.id],
        projectId = row[ProjectNotes.project_id],
        note = row[ProjectNotes.note],
        timestamp = row[ProjectNotes.created_at]
    )
}