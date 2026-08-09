package com.example.proyectdeswear.presentation

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseServiceWear {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val usersCollection = db.collection("users")

    private fun getTasksCollection(): CollectionReference? {
        val uid = auth.currentUser?.uid ?: return null
        return usersCollection.document(uid).collection("tareas")
    }

    /*
     * Envía al reloj todas las tareas pendientes del usuario autenticado.
     */
    fun listenTasks(onUpdate: (List<Task>) -> Unit) {
        val collection = getTasksCollection()
        if (collection == null) {
            onUpdate(emptyList())
            return
        }

        collection.addSnapshotListener { snapshot, error ->

            if (error != null) {
                onUpdate(emptyList())
                return@addSnapshotListener
            }

            val pendingTasks = snapshot
                ?.documents
                ?.mapNotNull { document ->
                    document.toObject(Task::class.java)?.apply {
                        documentId = document.id
                    }
                }
                ?.filter { !it.completado }
                ?.sortedWith(
                    compareBy<Task>(
                        { it.fecha.trim() },
                        { it.hora.trim() }
                    )
                )
                .orEmpty()

            onUpdate(pendingTasks)
        }
    }

    fun markTaskAsCompleted(
        task: Task,
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {}
    ) {
        val collection = getTasksCollection()
        if (collection == null || task.documentId.isBlank()) {
            onFailure()
            return
        }

        collection
            .document(task.documentId)
            .update(
                mapOf(
                    "completado" to true,
                    "completionTime" to System.currentTimeMillis()
                )
            )
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure()
            }
    }

    fun deleteTask(
        task: Task,
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {}
    ) {
        val collection = getTasksCollection()
        if (collection == null || task.documentId.isBlank()) {
            onFailure()
            return
        }

        collection
            .document(task.documentId)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure()
            }
    }

    fun createTask(
        task: Task,
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {}
    ) {
        val collection = getTasksCollection()
        if (collection == null) {
            onFailure()
            return
        }

        val documentReference = collection.document()

        val data = hashMapOf<String, Any?>(
            "documentId" to documentReference.id,
            "id" to task.id,
            "titulo" to task.titulo,
            "descripcion" to task.descripcion,
            "fecha" to task.fecha,
            "hora" to task.hora,
            "completado" to false,
            "completionTime" to null
        )

        documentReference
            .set(data)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure()
            }
    }
}
