package com.example.proyectodesdisint.streaming

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.Wearable

class WearSyncManager(
    context: Context
) {

    private val nodeClient = Wearable.getNodeClient(context)
    private val messageClient = Wearable.getMessageClient(context)

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
