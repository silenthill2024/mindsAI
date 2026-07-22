package com.example.mindsai.local.data

import androidx.room.*

@Dao
interface UserDao {

    @Insert
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

    @Query("UPDATE users SET xp = :newXp, level = :newLevel, tasksCompleted = :newCompleted WHERE id = :userId")
    suspend fun updateGamification(userId: Int, newXp: Int, newLevel: Int, newCompleted: Int)
}