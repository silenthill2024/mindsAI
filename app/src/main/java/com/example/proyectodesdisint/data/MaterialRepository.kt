package com.example.proyectodesdisint.data

import com.example.proyectodesdisint.model.MaterialApoyo
import com.example.proyectodesdisint.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class MaterialRepository {

    private val database =
        FirebaseFirestore.getInstance()

    private val materialCollection =
        database.collection("material_apoyo")

    private val usersCollection =
        database.collection("users")

    fun listenMaterials(
        onResult: (List<MaterialApoyo>) -> Unit,
        onError: (Exception) -> Unit = {}
    ): ListenerRegistration {
        return materialCollection
            .orderBy(
                "fecha",
                Query.Direction.DESCENDING
            )
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                val materials = snapshot
                    ?.documents
                    ?.mapNotNull { document ->
                        document
                            .toObject(MaterialApoyo::class.java)
                            ?.apply {
                                id = document.id
                            }
                    }
                    .orEmpty()

                onResult(materials)
            }
    }

    fun listenUserRole(
        uid: String,
        onResult: (User) -> Unit,
        onError: (Exception) -> Unit = {}
    ): ListenerRegistration {
        return usersCollection
            .document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                val user = snapshot
                    ?.toObject(User::class.java)
                    ?: User(
                        uid = uid,
                        role = "ALUMNO"
                    )

                onResult(user)
            }
    }

    suspend fun addLinkMaterial(
        material: MaterialApoyo
    ): Result<Unit> {
        return try {
            val document = materialCollection.document()

            val materialWithId = material.copy(
                id = document.id,
                tipo = "LINK"
            )

            document
                .set(materialWithId)
                .await()

            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    suspend fun deleteMaterial(
        materialId: String
    ): Result<Unit> {
        return try {
            materialCollection
                .document(materialId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}
