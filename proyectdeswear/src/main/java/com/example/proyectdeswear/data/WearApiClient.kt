package com.example.proyectdeswear.data

import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

object WearApiClient {

    private const val BASE_URL =
        "http://192.168.100.2:3001"

    private const val DEVICE_ID =
        "galaxy-watch-sm-l320"

    private val client =
        OkHttpClient()

    fun syncProfile(
        context: Context
    ) {

        val tokenRequest =
            Request.Builder()
                .url(
                    "$BASE_URL/wear/device/$DEVICE_ID/session"
                )
                .get()
                .build()

        client
            .newCall(tokenRequest)
            .enqueue(
                object : okhttp3.Callback {

                    override fun onFailure(
                        call: okhttp3.Call,
                        e: java.io.IOException
                    ) {
                        Log.e(
                            "MindsAIWearProfile",
                            "No se pudo obtener token",
                            e
                        )
                    }

                    override fun onResponse(
                        call: okhttp3.Call,
                        response: okhttp3.Response
                    ) {

                        response.use {

                            if (!it.isSuccessful) {

                                Log.e(
                                    "MindsAIWearProfile",
                                    "Token HTTP ${it.code}"
                                )

                                return
                            }

                            val body =
                                it.body
                                    ?.string()
                                    .orEmpty()

                            val json =
                                JSONObject(body)

                            val token =
                                json.optString("token")

                            if (
                                token.isBlank()
                            ) {
                                return
                            }

                            loadProfile(
                                context,
                                token
                            )
                        }
                    }
                }
            )
    }

    private fun loadProfile(
        context: Context,
        token: String
    ) {

        val request =
            Request.Builder()
                .url(
                    "$BASE_URL/wear/profile"
                )
                .header(
                    "Authorization",
                    "Bearer $token"
                )
                .get()
                .build()

        client
            .newCall(request)
            .enqueue(
                object : okhttp3.Callback {

                    override fun onFailure(
                        call: okhttp3.Call,
                        e: java.io.IOException
                    ) {

                        Log.e(
                            "MindsAIWearProfile",
                            "Error obteniendo perfil",
                            e
                        )
                    }

                    override fun onResponse(
                        call: okhttp3.Call,
                        response: okhttp3.Response
                    ) {

                        response.use {

                            if (!it.isSuccessful) {

                                Log.e(
                                    "MindsAIWearProfile",
                                    "Perfil HTTP ${it.code}"
                                )

                                return
                            }

                            val body =
                                it.body
                                    ?.string()
                                    .orEmpty()

                            val root =
                                JSONObject(body)

                            val data =
                                root.optJSONObject(
                                    "data"
                                )
                                    ?: return

                            val session =
                                WearUserSession(
                                    uid =
                                        data.optString(
                                            "uid"
                                        ),

                                    nombre =
                                        data.optString(
                                            "nombre"
                                        ),

                                    email =
                                        data.optString(
                                            "email"
                                        ),

                                    role =
                                        data.optString(
                                            "role",
                                            "ALUMNO"
                                        ),

                                    photoUrl =
                                        data.optString(
                                            "photoUrl"
                                        )
                                )

                            WearSessionStore(
                                context
                                    .applicationContext
                            )
                                .saveSession(
                                    session
                                )

                            Log.d(
                                "MindsAIWearProfile",
                                "Perfil sincronizado: ${session.nombre}"
                            )

                            Log.d(
                                "MindsAIWearProfile",
                                "Foto recibida: ${session.photoUrl.startsWith("data:image")}"
                            )
                        }
                    }
                }
            )
    }
}