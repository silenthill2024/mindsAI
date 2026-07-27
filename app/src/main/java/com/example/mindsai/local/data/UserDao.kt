package com.example.mindsai.local.data

import androidx.room.*

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(
        user: UserEntity
    )

    @Query(
        "SELECT * FROM users WHERE correo = :correo AND password = :password"
    )
    suspend fun login(
        correo:String,
        password:String
    ): UserEntity?

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET password = :newPassword WHERE id = :userId")
    suspend fun updatePassword(userId: Int, newPassword: String)

    @Query("UPDATE users SET studyHours = :hours, studyMinutes = :minutes WHERE id = :userId")
    suspend fun updateStudyTime(userId: Int, hours: Int, minutes: Int)

    @Query("UPDATE users SET averageGrade = :grade WHERE id = :userId")
    suspend fun updateAverageGrade(userId: Int, grade: Float)

    @Query("UPDATE users SET xp = :newXp, level = :newLevel, tasksCompleted = :newCompleted WHERE id = :userId")
    suspend fun updateGamification(userId: Int, newXp: Int, newLevel: Int, newCompleted: Int)
}