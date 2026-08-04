package com.example.proyectodesdisint.data

import android.net.Uri
import com.example.proyectodesdisint.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    suspend fun register(nombre: String, email: String, password: String): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: ""

            // Asignación de rol: 
            // ADMIN si el email es el de prueba, sino ALUMNO
            val role = if (email == "admin@mindsai.com") "ADMIN" else "ALUMNO"
            
            val user = User(
                uid = uid,
                nombre = nombre,
                email = email,
                role = role
            )

            db.collection("usuarios")
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

    suspend fun getUserProfile(): Result<User?> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.success(null)
            val snapshot = db.collection("usuarios").document(uid).get().await()
            val user = snapshot.toObject(User::class.java)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserProfile(
        nombre: String,
        descripcion: String,
        profileImageUri: Uri? = null
    ): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("No user logged in"))
            
            var profileImageUrl: String? = null
            
            // 1. Si hay una nueva imagen (Uri), subirla a Storage
            profileImageUri?.let { uri ->
                val fileRef = storage.reference.child("profiles/$uid.jpg")
                fileRef.putFile(uri).await()
                profileImageUrl = fileRef.downloadUrl.await().toString()
            }

            val updates = mutableMapOf(
                "nombre" to nombre,
                "descripcion" to descripcion
            )
            profileImageUrl?.let { updates["profileImageUrl"] = it }
            
            db.collection("usuarios").document(uid).update(updates.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }
}
