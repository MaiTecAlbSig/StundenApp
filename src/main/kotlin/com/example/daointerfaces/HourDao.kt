package com.example.daointerfaces

import com.example.datamodels.WorkHours

interface HourDao{
    suspend fun getAllHours(): List<WorkHours>
    suspend fun getHourByProject(id: Int): List<WorkHours>
    suspend fun getHourByEmployee(id: Int): List<WorkHours>
    suspend fun getHourById(id: Int): WorkHours?
    suspend fun getHourByDay(day: String): List<WorkHours>?
    suspend fun addHour(projectId:Int,employeeId: Int, hours: Double, day: String,description: String): WorkHours?
    suspend fun updateHour(id: Int, hours: Double?, day: String?,description: String?): Boolean
    suspend fun deleteHour(id: Int): Boolean
}