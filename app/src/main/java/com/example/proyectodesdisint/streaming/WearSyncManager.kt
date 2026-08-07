package com.example.proyectodesdisint.streaming

import android.content.Context
import android.util.Log
import com.example.proyectodesdisint.model.Task
import com.example.proyectodesdisint.model.User
import com.google.android.gms.wearable.Wearable
import org.json.JSONArray
import org.json.JSONObject

class WearSyncManager(
    context: Context
) {

    private val nodeClient =
        Wearable.getNodeClient(context)

    private val messageClient =
        Wearable.getMessageClient(context)

    fun syncUserSession(
        user: User
    ) {
        val data =
            JSONObject().apply {
                put("uid", user.uid)
                put("nombre", user.nombre)
                put("email", user.email)
                put("role", user.role)
                put("photoUrl", user.photoUrl)
            }

        sendTypedEvent(
            type = "USER_SESSION",
            data = data
        )
    }

    fun syncProfile(
        user: User
    ) {
        val data =
            JSONObject().apply {
                put("uid", user.uid)
                put("nombre", user.nombre)
                put("email", user.email)
                put("role", user.role)
                put("photoUrl", user.photoUrl)
            }

        sendTypedEvent(
            type = "PROFILE_SYNC",
            data = data
        )
    }

    fun syncTasks(
        tasks: List<Task>
    ) {
        val array = JSONArray()

        tasks.forEach { task ->

            array.put(
                JSONObject().apply {
                    put(
                        "id",
                        task.documentId.ifBlank {
                            task.id
                        }
                    )
                    put("titulo", task.titulo)
                    put(
                        "descripcion",
                        task.descripcion
                    )
                    put("fecha", task.fecha)
                    put("hora", task.hora)
                    put(
                        "prioridad",
                        task.prioridad
                    )
                    put(
                        "completado",
                        task.completado
                    )

                    put(
                        "asignadaPor",
                        extractAssignedBy(
                            task.descripcion
                        )
                    )
                }
            )
        }

        val data =
            JSONObject().apply {
                put("tasks", array)
            }

        sendTypedEvent(
            type = "TASKS_SYNC",
            data = data
        )
    }

    fun notifyAssignedTask(
        task: Task
    ) {
        val data =
            JSONObject().apply {
                put(
                    "id",
                    task.documentId.ifBlank {
                        task.id
                    }
                )
                put("titulo", task.titulo)
                put(
                    "descripcion",
                    task.descripcion
                )
                put("fecha", task.fecha)
                put("hora", task.hora)
                put(
                    "prioridad",
                    task.prioridad
                )
                put(
                    "completado",
                    task.completado
                )
                put(
                    "asignadaPor",
                    extractAssignedBy(
                        task.descripcion
                    )
                )
            }

        sendTypedEvent(
            type = "TASK_ASSIGNED",
            data = data
        )
    }

    fun syncTaskUpdated(
        task: Task
    ) {
        val data =
            JSONObject().apply {
                put(
                    "id",
                    task.documentId.ifBlank {
                        task.id
                    }
                )
                put("titulo", task.titulo)
                put(
                    "descripcion",
                    task.descripcion
                )
                put("fecha", task.fecha)
                put("hora", task.hora)
                put(
                    "prioridad",
                    task.prioridad
                )
                put(
                    "completado",
                    task.completado
                )
                put(
                    "asignadaPor",
                    extractAssignedBy(
                        task.descripcion
                    )
                )
            }

        sendTypedEvent(
            type = "TASK_UPDATED",
            data = data
        )
    }

    fun logoutWatch() {
        sendTypedEvent(
            type = "LOGOUT",
            data = JSONObject()
        )
    }

    private fun extractAssignedBy(
        description: String
    ): String {

        val regex =
            Regex(
                """\[Asignada por:\s*(.+?)]"""
            )

        return regex
            .find(description)
            ?.groupValues
            ?.getOrNull(1)
            ?.trim()
            ?: ""
    }

    private fun sendTypedEvent(
        type: String,
        data: JSONObject
    ) {
        val json =
            JSONObject().apply {
                put("type", type)
                put("data", data)
            }
                .toString()

        sendEventToWatch(
            json = json,
            path = "/stream_event"
        )
    }

    fun sendEventToWatch(
        json: String,
        path: String = "/stream_event"
    ) {
        nodeClient.connectedNodes
            .addOnSuccessListener { nodes ->

                if (nodes.isEmpty()) {
                    Log.w(
                        "MindsAIWear",
                        "No hay smartwatch conectado"
                    )

                    return@addOnSuccessListener
                }

                nodes.forEach { node ->

                    messageClient
                        .sendMessage(
                            node.id,
                            path,
                            json.toByteArray(
                                Charsets.UTF_8
                            )
                        )
                        .addOnSuccessListener {
                            Log.d(
                                "MindsAIWear",
                                "Evento enviado: $json"
                            )
                        }
                        .addOnFailureListener {
                            Log.e(
                                "MindsAIWear",
                                "Error enviando evento",
                                it
                            )
                        }
                }
            }
    }
}