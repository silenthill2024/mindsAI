package com.example.proyectodesdisint.streaming

import android.util.Log
import com.example.proyectodesdisint.data.ProfileImageService
import com.example.proyectodesdisint.model.User
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object WearApiClient {

    private const val BASE_URL =
        "http://192.168.100.2:3001"

    private const val DEVICE_ID =
        "galaxy-watch-sm-l320"

    private val client =
        OkHttpClient()

    private val profileImageService =
        ProfileImageService()

    fun registerUser(
        user: User
    ) {

        /*
         * Leemos la foto desde Firebase.
         * No usamos la ruta local guardada
         * en el dispositivo.
         */
        profileImageService.loadProfileImage(

            onSuccess = { firebasePhotoUrl ->

                val finalPhotoUrl =
                    firebasePhotoUrl.orEmpty()

                Log.d(
                    "MindsAIWearApi",
                    "Foto Firebase obtenida: " +
                        finalPhotoUrl.take(80)
                )

                sendUser(
                    user = user,
                    firebasePhotoUrl =
                        finalPhotoUrl
                )
            },

            onFailure = { error ->

                Log.e(
                    "MindsAIWearApi",
                    "Error leyendo foto Firebase",
                    error
                )

                /*
                 * Mandamos el usuario aunque
                 * falle la foto.
                 */
                sendUser(
                    user = user,
                    firebasePhotoUrl = ""
                )
            }
        )
    }

    private fun sendUser(
        user: User,
        firebasePhotoUrl: String
    ) {

        val json =
            JSONObject().apply {

                put(
                    "deviceId",
                    DEVICE_ID
                )

                put(
                    "uid",
                    user.uid
                )

                put(
                    "nombre",
                    user.nombre
                )

                put(
                    "email",
                    user.email
                )

                put(
                    "role",
                    user.role
                )

                put(
                    "photoUrl",
                    firebasePhotoUrl
                )
            }

        val body =
            json
                .toString()
                .toRequestBody(
                    "application/json"
                        .toMediaType()
                )

        val request =
            Request.Builder()
                .url(
                    "$BASE_URL/wear/auth"
                )
                .post(body)
                .build()

        client
            .newCall(request)
            .enqueue(

                object :
                    okhttp3.Callback {

                    override fun onFailure(
                        call: okhttp3.Call,
                        e: java.io.IOException
                    ) {

                        Log.e(
                            "MindsAIWearApi",
                            "Error registrando usuario",
                            e
                        )
                    }

                    override fun onResponse(
                        call: okhttp3.Call,
                        response:
                            okhttp3.Response
                    ) {

                        response.use {

                            Log.d(
                                "MindsAIWearApi",
                                "Wear auth HTTP ${it.code}"
                            )
                        }
                    }
                }
            )
    }
}