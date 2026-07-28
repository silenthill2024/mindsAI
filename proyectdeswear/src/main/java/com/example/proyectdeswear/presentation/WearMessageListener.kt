package com.example.proyectdeswear.presentation

import android.util.Log
import com.google.android.gms.wearable.*

class WearMessageListener(
    private val onTasksReceived: (List<String>) -> Unit
) : MessageClient.OnMessageReceivedListener {

    override fun onMessageReceived(messageEvent: MessageEvent) {

        if (messageEvent.path == "/tasks") {

            val data = String(messageEvent.data)
            val tasks = data.split("|")

            Log.d("WEAR", "RECIBIÓ TAREAS: $tasks")

            onTasksReceived(tasks)
        }
    }
}