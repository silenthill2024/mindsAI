package com.example.proyectdeswear.presentation

import android.content.Context
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseServiceWear(private val context: Context) {

    private val db = FirebaseFirestore.getInstance()

    private fun getTasksCollection(): CollectionReference {
        val prefs = context.getSharedPreferences("mindsai_prefs", Context.MODE_PRIVATE)
        val uid = prefs.getString("current_user_uid", "default_user") ?: "default_user"
        return db.collection("users").document(uid).collection("tareas")
    }

    /*
     * Envía al reloj todas las tareas pendientes.
     * MainActivity se encarga de clasificarlas en:
     * vencidas, hoy y próximas.
     */
    fun listenTasks(onUpdate: (List<Task>) -> Unit) {
        getTasksCollection().addSnapshotListener { snapshot, error ->

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
        if (task.documentId.isBlank()) {
            onFailure()
            return
        }

        getTasksCollection()
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
        if (task.documentId.isBlank()) {
            onFailure()
            return
        }

        getTasksCollection()
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
        val documentReference = getTasksCollection().document()

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
