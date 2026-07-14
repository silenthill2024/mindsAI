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
}