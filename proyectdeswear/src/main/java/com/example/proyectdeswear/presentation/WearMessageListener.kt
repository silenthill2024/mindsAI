package com.example.proyectdeswear.presentation

import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject

class WearMessageListener : WearableListenerService() {

    private val db = FirebaseFirestore.getInstance()
    private val tasksCollection = db.collection("tasks")

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        if (messageEvent.path != "/stream_event") {
            return
        }

        val jsonText = String(
            messageEvent.data,
            Charsets.UTF_8
        )

        Log.d(
            "MindsAIWear",
            "Streaming recibido: $jsonText"
        )

        try {
            val root = JSONObject(jsonText)
            val type = root.optString("type")
            val data = root.optJSONObject("data") ?: JSONObject()

            when (type) {

                "TASK_CREATED" -> {
                    val documentId = data.optString("documentId")

                    if (documentId.isBlank()) {
                        Log.e(
                            "MindsAIWear",
                            "TASK_CREATED sin documentId"
                        )
                        return
                    }

                    val taskData = hashMapOf<String, Any?>(
                        "documentId" to documentId,
                        "titulo" to data.optString("titulo"),
                        "descripcion" to data.optString("descripcion"),
                        "fecha" to data.optString("fecha"),
                        "hora" to data.optString("hora"),
                        "prioridad" to data.optString("prioridad", "Media"),
                        "completado" to data.optBoolean("completado", false),
                        "completionTime" to null
                    )

                    tasksCollection
                        .document(documentId)
                        .set(taskData)
                        .addOnSuccessListener {
                            Log.d(
                                "MindsAIWear",
                                "TASK_CREATED sincronizada"
                            )
                        }
                        .addOnFailureListener { error ->
                            Log.e(
                                "MindsAIWear",
                                "Error TASK_CREATED",
                                error
                            )
                        }
                }

                "TASK_UPDATED" -> {
                    val documentId = data.optString("documentId")

                    if (documentId.isBlank()) {
                        Log.e(
                            "MindsAIWear",
                            "TASK_UPDATED sin documentId"
                        )
                        return
                    }

                    val updates = hashMapOf<String, Any?>(
                        "titulo" to data.optString("titulo"),
                        "descripcion" to data.optString("descripcion"),
                        "fecha" to data.optString("fecha"),
                        "hora" to data.optString("hora"),
                        "prioridad" to data.optString("prioridad", "Media"),
                        "completado" to data.optBoolean("completado", false),
                        "completionTime" to data.optLong("completionTime", 0L)
                    )

                    tasksCollection
                        .document(documentId)
                        .update(updates)
                        .addOnSuccessListener {
                            Log.d(
                                "MindsAIWear",
                                "TASK_UPDATED sincronizada"
                            )
                        }
                        .addOnFailureListener { error ->
                            Log.e(
                                "MindsAIWear",
                                "Error TASK_UPDATED",
                                error
                            )
                        }
                }

                "TASK_DELETED" -> {
                    val documentId = data.optString("documentId")

                    if (documentId.isBlank()) {
                        Log.e(
                            "MindsAIWear",
                            "TASK_DELETED sin documentId"
                        )
                        return
                    }

                    tasksCollection
                        .document(documentId)
                        .delete()
                        .addOnSuccessListener {
                            Log.d(
                                "MindsAIWear",
                                "TASK_DELETED sincronizada"
                            )
                        }
                        .addOnFailureListener { error ->
                            Log.e(
                                "MindsAIWear",
                                "Error TASK_DELETED",
                                error
                            )
                        }
                }

                else -> {
                    Log.w(
                        "MindsAIWear",
                        "Evento desconocido: $type"
                    )
                }
            }

        } catch (error: Exception) {
            Log.e(
                "MindsAIWear",
                "JSON inválido",
                error
            )
        }
    }
}
