package com.example.proyectdeswear.presentation

import android.content.Context
import android.util.Log
import com.example.proyectdeswear.data.WearSessionStore
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseServiceWear(
    private val context: Context
) {

    private val db =
        FirebaseFirestore.getInstance()

    private fun getTasksCollection():
        CollectionReference? {

        val session =
            WearSessionStore(
                context.applicationContext
            ).getSession()

        val uid =
            session.uid.trim()

        if (uid.isBlank()) {

            Log.w(
                "MindsAIWearTasks",
                "No hay UID sincronizado en el reloj"
            )

            return null
        }

        Log.d(
            "MindsAIWearTasks",
            "Cargando tareas del UID: $uid"
        )

        return db
            .collection("users")
            .document(uid)
            .collection("tareas")
    }

    fun listenTasks(
        onUpdate: (List<Task>) -> Unit
    ) {

        val collection =
            getTasksCollection()

        if (collection == null) {

            onUpdate(
                emptyList()
            )

            return
        }

        collection
            .addSnapshotListener {
                    snapshot,
                    error ->

                if (error != null) {

                    Log.e(
                        "MindsAIWearTasks",
                        "Error leyendo tareas",
                        error
                    )

                    onUpdate(
                        emptyList()
                    )

                    return@addSnapshotListener
                }

                val tasks =
                    snapshot
                        ?.documents
                        ?.mapNotNull {
                                document ->

                            document
                                .toObject(
                                    Task::class.java
                                )
                                ?.also {
                                    task ->

                                    task.documentId =
                                        document.id
                                }
                        }
                        ?.filter {
                            !it.completado
                        }
                        .orEmpty()

                Log.d(
                    "MindsAIWearTasks",
                    "Tareas recibidas: ${tasks.size}"
                )

                onUpdate(
                    tasks
                )
            }
    }

    fun markTaskAsCompleted(
        task: Task,
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {}
    ) {

        if (
            task.documentId
                .isBlank()
        ) {

            onFailure()

            return
        }

        val collection =
            getTasksCollection()

        if (
            collection == null
        ) {

            onFailure()

            return
        }

        collection
            .document(
                task.documentId
            )
            .update(
                mapOf(
                    "completado" to true,
                    "completionTime" to
                        System.currentTimeMillis()
                )
            )
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                error ->

                Log.e(
                    "MindsAIWearTasks",
                    "Error completando tarea",
                    error
                )

                onFailure()
            }
    }

    fun deleteTask(
        task: Task,
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {}
    ) {

        if (
            task.documentId
                .isBlank()
        ) {

            onFailure()

            return
        }

        val collection =
            getTasksCollection()

        if (
            collection == null
        ) {

            onFailure()

            return
        }

        collection
            .document(
                task.documentId
            )
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                error ->

                Log.e(
                    "MindsAIWearTasks",
                    "Error eliminando tarea",
                    error
                )

                onFailure()
            }
    }
}