package com.example.daointerfaces

import com.example.datamodels.Project

interface ProjectDao{
    suspend fun getAll(): List<Project>
    suspend fun getByEmployeeId(projectIds: List<Int>): List<Project>
    suspend fun getById(id: Int): Project?
    suspend fun createProject(name: String, description: String?,customerId: Int?, startDate: String?, endDate: String?): Project?
    suspend fun updateProject(id: Int, name: String?, description: String?,customerId: Int?, startDate: String?, endDate: String?): Boolean
    suspend fun deleteProject(id: Int): Boolean
}