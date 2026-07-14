package com.example.mindsai.repository

import com.example.mindsai.local.data.UserDao
import com.example.mindsai.local.data.UserEntity

class UserRepository(private val userDao: UserDao) {

    suspend fun register(user: UserEntity) {
        userDao.insertUser(user)
    }

    suspend fun login(correo: String, password: String): UserEntity? {
        return userDao.login(correo, password)
    }

    suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user)
    }
}
