package com.example.proyectodesdisint.streaming

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.Wearable

class WearSyncManager(
    context: Context
) {

    private val nodeClient = Wearable.getNodeClient(context)
    private val messageClient = Wearable.getMessageClient(context)

    fun syncUserSession(uid: String) {
        val json = """
            {
              "type": "USER_SESSION",
              "data": { "uid": "$uid" }
            }
        """.trimIndent()
        sendEventToWatch(json, "/stream_event")
    }

    fun syncUserProfile(profile: com.example.proyectodesdisint.model.UserProfile) {
        val json = """
            {
              "type": "USER_PROFILE",
              "data": {
                "uid": "${profile.uid}",
                "nombre": "${profile.nombre}",
                "email": "${profile.email}",
                "role": "${profile.role}",
                "nivel": ${profile.nivel},
                "xp": ${profile.xp},
                "xpMax": ${profile.xpMax},
                "photoUrl": "${profile.photoUrl}"
              }
            }
        """.trimIndent()
        sendEventToWatch(json, "/stream_event")
    }

    fun syncAcademicProgress(progress: Int, level: Int, xp: Int) {
        val json = """
            {
              "type": "ACADEMIC_UPDATE",
              "data": { 
                "progress": $progress, 
                "level": $level, 
                "xp": $xp 
              }
            }
        """.trimIndent()
        sendEventToWatch(json, "/stream_event")
    }

    fun forceResetWatch(uid: String) {
        val json = """
            {
              "type": "FORCE_RESET",
              "data": { "uid": "$uid", "timestamp": ${System.currentTimeMillis()} }
            }
        """.trimIndent()
        sendEventToWatch(json, "/stream_event")
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

                    messageClient.sendMessage(
                        node.id,
                        path,
                        json.toByteArray(Charsets.UTF_8)
                    )
                        .addOnSuccessListener {
                            Log.d(
                                "MindsAIWear",
                                "Evento enviado al reloj: ${node.displayName}"
                            )
                        }
                        .addOnFailureListener { error ->
                            Log.e(
                                "MindsAIWear",
                                "Error enviando evento al reloj",
                                error
                            )
                        }
                }
            }
            .addOnFailureListener { error ->
                Log.e(
                    "MindsAIWear",
                    "Error buscando smartwatch",
                    error
                )
            }
    }
}
