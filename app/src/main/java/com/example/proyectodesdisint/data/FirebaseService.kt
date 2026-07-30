package com.example.proyectodesdisint.data

import com.google.firebase.firestore.FirebaseFirestore
import com.example.proyectodesdisint.model.Task

class FirebaseService {

    private val db = FirebaseFirestore.getInstance()
    private val tasksCollection = db.collection("tasks")

    fun addTask(
        task: Task,
        onSuccess: (Task) -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val docRef = tasksCollection.document()
        val taskWithId = task.copy(documentId = docRef.id)

        docRef.set(taskWithId)
            .addOnSuccessListener {
                onSuccess(taskWithId)
            }
            .addOnFailureListener { error ->
                onFailure(error)
            }
    }

    fun listenTasks(onResult: (List<Task>) -> Unit) {
        tasksCollection.addSnapshotListener { snapshot, _ ->
            val tasks = snapshot?.mapNotNull { doc ->
                val task = doc.toObject(Task::class.java)
                task.documentId = doc.id
                task
            } ?: emptyList()

            onResult(tasks)
        }
    }

    fun updateTask(
        task: Task,
        onSuccess: (Task) -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        tasksCollection.document(task.documentId)
            .update(
                mapOf(
                    "titulo" to task.titulo,
                    "descripcion" to task.descripcion,
                    "fecha" to task.fecha,
                    "hora" to task.hora,
                    "completado" to task.completado,
                    "completionTime" to task.completionTime
                )
            )
            .addOnSuccessListener {
                onSuccess(task)
            }
            .addOnFailureListener { error ->
                onFailure(error)
            }
    }

    fun deleteTask(
        documentId: String,
        onSuccess: (String) -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        tasksCollection.document(documentId)
            .delete()
            .addOnSuccessListener {
                onSuccess(documentId)
            }
            .addOnFailureListener { error ->
                onFailure(error)
            }
    }
}