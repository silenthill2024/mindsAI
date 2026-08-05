package com.example.proyectodesdisint.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.example.proyectodesdisint.model.Task
import kotlinx.coroutines.tasks.await

class TareaRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getUserId(): String {
        return auth.currentUser?.uid ?: ""
    }

    suspend fun agregarTarea(tarea: Task) {
        val uid = getUserId()

        db.collection("users")
            .document(uid)
            .collection("tareas")
            .add(tarea)
            .await()
    }

    suspend fun obtenerTareas(): List<Task> {
        val uid = getUserId()

        val result = db.collection("users")
            .document(uid)
            .collection("tareas")
            .get()
            .await()

        return result.documents.map { doc ->
            Task(
                id = doc.id,
                titulo = doc.getString("titulo") ?: "",
                descripcion = doc.getString("descripcion") ?: "",
                fecha = doc.getString("fecha") ?: ""
            )
        }
    }
}
