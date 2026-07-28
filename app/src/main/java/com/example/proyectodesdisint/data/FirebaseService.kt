package com.example.proyectodesdisint.data

import com.google.firebase.firestore.FirebaseFirestore
import com.example.proyectodesdisint.model.Task

class FirebaseService {

    private val db = FirebaseFirestore.getInstance()
    private val tasksCollection = db.collection("tasks")

    fun addTask(task: Task) {
        val docRef = tasksCollection.document()
        val taskWithId = task.copy(documentId = docRef.id)

        docRef.set(taskWithId)
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

    fun updateTask(task: Task) {
        tasksCollection.document(task.documentId).update(
            mapOf(
                "titulo" to task.titulo,
                "descripcion" to task.descripcion,
                "fecha" to task.fecha,
                "hora" to task.hora,
                "completado" to task.completado,
                "completionTime" to task.completionTime
            )
        )
    }

    fun deleteTask(documentId: String) {
        tasksCollection.document(documentId).delete()
    }
}