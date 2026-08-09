package com.example.proyectodesdisint.data

import com.example.proyectodesdisint.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    suspend fun register(nombre: String, email: String, password: String): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: ""

            val user = User(uid, nombre, email)

            db.collection("users")
                .document(uid)
                .set(user)
                .await()

            Result.success(uid)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun login(email: String, password: String): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user?.uid ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    fun logout() {
        auth.signOut()
    }
}
