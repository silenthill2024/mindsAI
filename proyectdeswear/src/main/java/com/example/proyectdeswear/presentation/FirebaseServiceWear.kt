package com.example.proyectdeswear.presentation

import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class FirebaseServiceWear {

    private val db = FirebaseFirestore.getInstance()
    private val tasksCollection = db.collection("tasks")

    fun listenTasks(onUpdate: (List<Task>) -> Unit) {
        tasksCollection.addSnapshotListener { snapshot, _ ->
            val timeZone = TimeZone.getDefault()
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
                this.timeZone = timeZone
            }.format(Date())

            val allTasks = snapshot?.documents?.mapNotNull { doc ->
                val task = doc.toObject(Task::class.java)
                task?.apply { documentId = doc.id }
            }.orEmpty()

            val pendingTasks = allTasks.filter { !it.completado }
            val todayTasks = pendingTasks.filter { isTaskDueToday(it.fecha, today) }
            onUpdate(todayTasks)
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

        tasksCollection.document(task.documentId).update(
            mapOf(
                "completado" to true,
                "completionTime" to System.currentTimeMillis()
            )
        )
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }
    }

    private fun isTaskDueToday(taskDate: String, today: String): Boolean {
        val normalizedDate = taskDate.trim()

        if (normalizedDate.isBlank()) return false
        if (normalizedDate == today) return true

        return normalizedDate.take(10) == today
    }
}
