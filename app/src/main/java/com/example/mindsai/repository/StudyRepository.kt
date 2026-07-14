package com.example.mindsai.repository

import com.example.mindsai.local.data.StudyDao
import com.example.mindsai.local.data.TaskEntity
import kotlinx.coroutines.flow.Flow

class StudyRepository(private val studyDao: StudyDao) {

    fun getTasksForUser(userId: Int): Flow<List<TaskEntity>> {
        return studyDao.getTasksForUser(userId)
    }

    suspend fun addTask(task: TaskEntity) {
        studyDao.insertTask(task)
    }

    suspend fun updateTask(task: TaskEntity) {
        studyDao.updateTask(task)
    }
}
