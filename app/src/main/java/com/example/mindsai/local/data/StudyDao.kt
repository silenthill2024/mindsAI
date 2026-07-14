package com.example.mindsai.local.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyDao {
    
    // Relational: Get subjects for a specific user
    @Query("SELECT * FROM subjects WHERE userId = :userId")
    fun getSubjectsForUser(userId: Int): Flow<List<SubjectEntity>>

    @Insert
    suspend fun insertSubject(subject: SubjectEntity)

    // Not Related: Standalone operations for quotes
    @Query("SELECT * FROM quotes")
    fun getAllQuotes(): Flow<List<QuoteEntity>>

    @Insert
    suspend fun insertQuote(quote: QuoteEntity)

    @Query("SELECT * FROM quotes ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomQuote(): QuoteEntity?

    // --- Tasks (Relational with User) ---
    @Query("SELECT * FROM tasks WHERE userId = :userId")
    fun getTasksForUser(userId: Int): Flow<List<TaskEntity>>

    @Insert
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)
}
