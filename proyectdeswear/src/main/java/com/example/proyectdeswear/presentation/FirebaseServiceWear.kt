package com.example.proyectdeswear.presentation

import com.google.firebase.firestore.FirebaseFirestore

class FirebaseServiceWear {

    private val db = FirebaseFirestore.getInstance()
    private val tasksCollection = db.collection("tasks")

    /*
     * Envía al reloj todas las tareas pendientes.
     * MainActivity se encarga de clasificarlas en:
     * vencidas, hoy y próximas.
     */
    fun listenTasks(onUpdate: (List<Task>) -> Unit) {
        tasksCollection.addSnapshotListener { snapshot, error ->

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

        tasksCollection
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

        tasksCollection
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
        val documentReference = tasksCollection.document()

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
