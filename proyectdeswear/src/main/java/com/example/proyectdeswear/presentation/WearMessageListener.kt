package com.example.proyectdeswear.presentation

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject

class WearMessageListener : WearableListenerService() {

    private val db = FirebaseFirestore.getInstance()

    private fun getTasksCollection(): com.google.firebase.firestore.CollectionReference {
        val prefs = getSharedPreferences("mindsai_prefs", Context.MODE_PRIVATE)
        val uid = prefs.getString("current_user_uid", "default_user") ?: "default_user"
        return db.collection("users").document(uid).collection("tareas")
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        if (messageEvent.path != "/stream_event") return

        val jsonText = String(
            messageEvent.data,
            Charsets.UTF_8
        )

        try {
            val root = JSONObject(jsonText)
            val type = root.optString("type")
            val data = root.optJSONObject("data") ?: JSONObject()

            when (type) {
                "USER_SESSION" -> {
                    val uid = data.optString("uid")
                    if (uid.isNotBlank()) {
                        val prefs = getSharedPreferences("mindsai_prefs", Context.MODE_PRIVATE)
                        prefs.edit().putString("current_user_uid", uid).apply()
                        Log.d("MindsAIWear", "Sesión de usuario sincronizada: $uid")
                    }
                }

                "USER_PROFILE" -> {
                    val prefs = getSharedPreferences("mindsai_prefs", Context.MODE_PRIVATE)
                    prefs.edit().apply {
                        putString("user_name", data.optString("nombre"))
                        putString("user_email", data.optString("email"))
                        putString("user_role", data.optString("role"))
                        putInt("user_level", data.optInt("nivel", 1))
                        putInt("user_xp", data.optInt("xp", 0))
                        putString("user_photo", data.optString("photoUrl"))
                        apply()
                    }
                    Log.d("MindsAIWear", "Perfil de usuario sincronizado en el reloj")
                }

                "FORCE_RESET" -> {
                    val prefs = getSharedPreferences("mindsai_prefs", Context.MODE_PRIVATE)
                    prefs.edit().clear().apply()
                    Log.d("MindsAIWear", "Reloj restablecido por comando forzado")
                }

                "ACADEMIC_UPDATE" -> {
                    val prefs = getSharedPreferences("mindsai_prefs", Context.MODE_PRIVATE)
                    prefs.edit().apply {
                        putInt("academic_progress", data.optInt("progress", 0))
                        // El nivel/xp pueden venir aquí o en USER_PROFILE
                        if (data.has("level")) putInt("user_level", data.optInt("level"))
                        apply()
                    }
                    Log.d("MindsAIWear", "Progreso académico sincronizado: ${data.optInt("progress")}%")
                }

                "TASK_CREATED" -> {
                    syncCreatedTask(data)

                    showNotification(
                        title = "Nueva tarea",
                        message = data.optString(
                            "titulo",
                            "Se agregó una tarea nueva"
                        )
                    )
                }

                "TASK_UPDATED" -> {
                    syncUpdatedTask(data)
                }

                "TASK_DELETED" -> {
                    deleteTask(data)
                }

                "BLOG_POST_CREATED" -> {
                    val author = data.optString(
                        "autor",
                        "Comunidad MindsAI"
                    )

                    val postTitle = data.optString(
                        "titulo",
                        "Nueva publicación"
                    )

                    showNotification(
                        title = "Nueva publicación",
                        message = "$author publicó: $postTitle"
                    )
                }

                "BLOG_REPLY_CREATED" -> {
                    val author = data.optString(
                        "autor",
                        "Usuario"
                    )

                    val postTitle = data.optString(
                        "titulo",
                        "una publicación"
                    )

                    showNotification(
                        title = "Nuevo comentario",
                        message = "$author comentó en $postTitle"
                    )
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

    private fun syncCreatedTask(data: JSONObject) {
        val documentId = data.optString("documentId")

        if (documentId.isBlank()) return

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

        getTasksCollection()
            .document(documentId)
            .set(taskData)
    }

    private fun syncUpdatedTask(data: JSONObject) {
        val documentId = data.optString("documentId")

        if (documentId.isBlank()) return

        val updates = hashMapOf<String, Any?>(
            "titulo" to data.optString("titulo"),
            "descripcion" to data.optString("descripcion"),
            "fecha" to data.optString("fecha"),
            "hora" to data.optString("hora"),
            "prioridad" to data.optString("prioridad", "Media"),
            "completado" to data.optBoolean("completado", false),
            "completionTime" to data.optLong("completionTime", 0L)
        )

        getTasksCollection()
            .document(documentId)
            .update(updates)
    }

    private fun deleteTask(data: JSONObject) {
        val documentId = data.optString("documentId")

        if (documentId.isBlank()) return

        getTasksCollection()
            .document(documentId)
            .delete()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "MindsAI",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description =
                    "Tareas y actividad de Comunidad MindsAI"

                enableVibration(true)
            }

            val manager = getSystemService(
                NotificationManager::class.java
            )

            manager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(
        title: String,
        message: String
    ) {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w(
                "MindsAIWear",
                "Permiso de notificaciones no concedido"
            )
            return
        }

        val notification = NotificationCompat.Builder(
            this,
            CHANNEL_ID
        )
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 250, 150, 250))
            .build()

        val manager = getSystemService(
            NotificationManager::class.java
        )

        manager.notify(
            System.currentTimeMillis().toInt(),
            notification
        )
    }

    companion object {
        private const val CHANNEL_ID =
            "mindsai_notifications"
    }
}
