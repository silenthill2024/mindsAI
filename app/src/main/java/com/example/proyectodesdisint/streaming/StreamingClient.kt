package com.example.proyectodesdisint.streaming

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class StreamingClient(
    private val onMessageReceived: (String) -> Unit
) {

    private val client = OkHttpClient()

    private var webSocket: WebSocket? = null

    fun connect() {

        val request = Request.Builder()
            .url("ws://192.168.100.2:3000/stream")
            .build()

        webSocket = client.newWebSocket(
            request,
            object : WebSocketListener() {

                override fun onOpen(
                    webSocket: WebSocket,
                    response: Response
                ) {
                    Log.d("MindsAIStreaming", "WebSocket conectado")
                }

                override fun onMessage(
                    webSocket: WebSocket,
                    text: String
                ) {
                    Log.d("MindsAIStreaming", "Evento recibido: $text")

                    onMessageReceived(text)
                }

                override fun onClosing(
                    webSocket: WebSocket,
                    code: Int,
                    reason: String
                ) {
                    Log.d("MindsAIStreaming", "WebSocket cerrando: $reason")

                    webSocket.close(code, reason)
                }

                override fun onFailure(
                    webSocket: WebSocket,
                    t: Throwable,
                    response: Response?
                ) {
                    Log.e(
                        "MindsAIStreaming",
                        "Error WebSocket",
                        t
                    )
                }
            }
        )
    }

    fun send(message: String): Boolean {
        return webSocket?.send(message) ?: false
    }

    fun disconnect() {
        webSocket?.close(
            1000,
            "MindsAI cerrado"
        )
    }
}
