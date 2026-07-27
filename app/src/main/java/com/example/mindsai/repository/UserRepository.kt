package com.example.mindsai.repository

import com.example.mindsai.local.SupabaseClient
import com.example.mindsai.local.data.UserDao
import com.example.mindsai.local.data.UserEntity
import io.github.jan.supabase.postgrest.postgrest

class UserRepository(private val userDao: UserDao) {

    private val profileTable = SupabaseClient.client.postgrest["profiles"]

    suspend fun register(user: UserEntity) {
        try {
            // 1. Guardar en Supabase (Remoto)
            profileTable.insert(user)
            
            // 2. Guardar en Room (Local) para velocidad
            userDao.insertUser(user)
        } catch (e: Exception) {
            e.printStackTrace()
            // Si falla internet, al menos guardamos en local (opcional)
            userDao.insertUser(user)
        }
    }

    suspend fun login(correo: String, password: String): UserEntity? {
        return try {
            // 1. Intentar buscar en Supabase (Para que funcione en cualquier celular)
            val remoteUser = profileTable.select {
                filter {
                    eq("correo", correo)
                    eq("password", password)
                }
            }.decodeSingleOrNull<UserEntity>()

            if (remoteUser != null) {
                // 2. Si lo encuentra, lo guardamos/actualizamos en el Room de este celular
                userDao.insertUser(remoteUser)
                remoteUser
            } else {
                // 3. Si no hay internet o no está en la nube, buscar en local
                userDao.login(correo, password)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Si hay error de red, buscar en local
            userDao.login(correo, password)
        }
    }

    suspend fun updateUser(user: UserEntity) {
        try {
            userDao.updateUser(user)
            profileTable.update({
                set("nombre", user.nombre)
                set("description", user.description)
            }) {
                filter { eq("id", user.id) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updateGamification(userId: Int, xp: Int, level: Int, completed: Int) {
        try {
            userDao.updateGamification(userId, xp, level, completed)
            profileTable.update({
                set("xp", xp)
                set("level", level)
                set("tasksCompleted", completed)
            }) {
                filter { eq("id", userId) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updatePassword(userId: Int, newPassword: String) {
        try {
            userDao.updatePassword(userId, newPassword)
            profileTable.update({
                set("password", newPassword)
            }) {
                filter { eq("id", userId) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updateStudyTime(userId: Int, hours: Int, minutes: Int) {
        try {
            userDao.updateStudyTime(userId, hours, minutes)
            profileTable.update({
                set("studyHours", hours)
                set("studyMinutes", minutes)
            }) {
                filter { eq("id", userId) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updateAverageGrade(userId: Int, grade: Float) {
        try {
            userDao.updateAverageGrade(userId, grade)
            profileTable.update({
                set("averageGrade", grade)
            }) {
                filter { eq("id", userId) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
