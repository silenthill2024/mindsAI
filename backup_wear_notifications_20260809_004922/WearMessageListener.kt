package com.example.proyectdeswear.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.example.proyectdeswear.R
import com.example.proyectdeswear.data.WearSessionStore
import com.example.proyectdeswear.data.WearNotificationStore
import com.example.proyectdeswear.data.WearTask
import com.example.proyectdeswear.data.WearUserSession
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import org.json.JSONArray
import org.json.JSONObject

class WearMessageListener : WearableListenerService() {

    companion object {
        private const val CHANNEL_ID = "mindsai_tasks"
        private const val PREFS_TASKS = "mindsai_wear_tasks"
        private const val KEY_TASKS = "tasks_json"
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        val payload = messageEvent.data
            .toString(Charsets.UTF_8)

        try {
            val root = JSONObject(payload)

            when (root.optString("type")) {

                "USER_SESSION" -> {
                    handleUserSession(root)
                }

                "PROFILE_SYNC" -> {
                    handleProfileSync(root)
                }

                "TASKS_SYNC" -> {
                    handleTasksSync(root)
                }

                "TASK_ASSIGNED" -> {
                    handleTaskAssigned(root)
                }

                "TASK_UPDATED" -> {
                    handleTaskUpdated(root)
                }

                "BLOG_COMMENT" -> {
                    handleRelevantNotification(
                        root = root,
                        defaultTitle = "Nuevo comentario",
                        defaultMessage = "Respondieron en Comunidad",
                        urgent = false
                    )
                }

                "NEW_MATERIAL" -> {
                    handleRelevantNotification(
                        root = root,
                        defaultTitle = "Material nuevo",
                        defaultMessage = "Se agregÃ³ nuevo material de apoyo",
                        urgent = false
                    )
                }

                "TASK_DUE" -> {
                    handleRelevantNotification(
                        root = root,
                        defaultTitle = "Tarea por vencer",
                        defaultMessage = "Tienes una tarea prÃ³xima a vencer",
                        urgent = true
                    )
                }

                "LOGOUT" -> {
                    clearSessionAndTasks()
                }
            }

        } catch (_: Exception) {
            // Ignoramos mensajes que no tengan el formato esperado.
        }
    }

    private fun handleUserSession(
        root: JSONObject
    ) {
        val data = root.optJSONObject("data")
            ?: return

        val session = WearUserSession(
            uid = data.optString("uid"),
            nombre = data.optString("nombre"),
            email = data.optString("email"),
            role = data.optString(
                "role",
                "ALUMNO"
            ),
            photoUrl = data.optString(
                "photoUrl"
            )
        )

        WearSessionStore(this)
            .saveSession(session)
    }

    private fun handleProfileSync(
        root: JSONObject
    ) {
        val data = root.optJSONObject("data")
            ?: return

        val current =
            WearSessionStore(this)
                .getSession()

        val updated =
            current.copy(
                nombre = data.optString(
                    "nombre",
                    current.nombre
                ),
                email = data.optString(
                    "email",
                    current.email
                ),
                role = data.optString(
                    "role",
                    current.role
                ),
                photoUrl = data.optString(
                    "photoUrl",
                    current.photoUrl
                )
            )

        WearSessionStore(this)
            .saveSession(updated)
    }

    private fun handleTasksSync(
        root: JSONObject
    ) {
        val data =
            root.optJSONObject("data")
                ?: return

        val tasks =
            data.optJSONArray("tasks")
                ?: JSONArray()

        saveTasks(tasks)
    }

    private fun handleTaskAssigned(
        root: JSONObject
    ) {
        val data =
            root.optJSONObject("data")
                ?: return

        val task =
            WearTask(
                id = data.optString("id"),
                titulo = data.optString("titulo"),
                descripcion = data.optString("descripcion"),
                fecha = data.optString("fecha"),
                hora = data.optString("hora"),
                prioridad = data.optString("prioridad"),
                completado = data.optBoolean(
                    "completado",
                    false
                ),
                asignadaPor =
                    data.optString("asignadaPor")
            )

        appendTask(task)

        WearNotificationStore(this)
            .add(
                type =
                    "TASK_DUE",
                title =
                    "Nueva tarea",
                message =
                    task.titulo
            )

        vibrateWatch(
            urgent = true
        )

        showTaskNotification(task)
    }

    private fun handleTaskUpdated(
        root: JSONObject
    ) {
        val data =
            root.optJSONObject("data")
                ?: return

        val id =
            data.optString("id")

        if (id.isBlank()) {
            return
        }

        val current =
            getTasks()
                .toMutableList()

        val index =
            current.indexOfFirst {
                it.id == id
            }

        if (index >= 0) {

            current[index] =
                current[index].copy(
                    titulo =
                        data.optString(
                            "titulo",
                            current[index].titulo
                        ),
                    descripcion =
                        data.optString(
                            "descripcion",
                            current[index].descripcion
                        ),
                    fecha =
                        data.optString(
                            "fecha",
                            current[index].fecha
                        ),
                    hora =
                        data.optString(
                            "hora",
                            current[index].hora
                        ),
                    prioridad =
                        data.optString(
                            "prioridad",
                            current[index].prioridad
                        ),
                    completado =
                        if (
                            data.has("completado")
                        ) {
                            data.optBoolean(
                                "completado"
                            )
                        } else {
                            current[index]
                                .completado
                        },
                    asignadaPor =
                        data.optString(
                            "asignadaPor",
                            current[index]
                                .asignadaPor
                        )
                )

            saveTaskList(current)
        }
    }

    private fun clearSessionAndTasks() {
        WearSessionStore(this)
            .clearSession()

        getSharedPreferences(
            PREFS_TASKS,
            Context.MODE_PRIVATE
        )
            .edit()
            .clear()
            .apply()
    }

    private fun saveTasks(
        array: JSONArray
    ) {
        getSharedPreferences(
            PREFS_TASKS,
            Context.MODE_PRIVATE
        )
            .edit()
            .putString(
                KEY_TASKS,
                array.toString()
            )
            .apply()
    }

    private fun appendTask(
        task: WearTask
    ) {
        val tasks =
            getTasks()
                .toMutableList()

        val index =
            tasks.indexOfFirst {
                it.id == task.id &&
                    task.id.isNotBlank()
            }

        if (index >= 0) {
            tasks[index] = task
        } else {
            tasks.add(0, task)
        }

        saveTaskList(tasks)
    }

    private fun getTasks(): List<WearTask> {

        val json =
            getSharedPreferences(
                PREFS_TASKS,
                Context.MODE_PRIVATE
            )
                .getString(
                    KEY_TASKS,
                    "[]"
                )
                ?: "[]"

        val array =
            try {
                JSONArray(json)
            } catch (_: Exception) {
                JSONArray()
            }

        return buildList {

            for (
                index in 0 until array.length()
            ) {

                val item =
                    array.optJSONObject(index)
                        ?: continue

                add(
                    WearTask(
                        id =
                            item.optString("id"),
                        titulo =
                            item.optString("titulo"),
                        descripcion =
                            item.optString(
                                "descripcion"
                            ),
                        fecha =
                            item.optString("fecha"),
                        hora =
                            item.optString("hora"),
                        prioridad =
                            item.optString(
                                "prioridad"
                            ),
                        completado =
                            item.optBoolean(
                                "completado",
                                false
                            ),
                        asignadaPor =
                            item.optString(
                                "asignadaPor"
                            )
                    )
                )
            }
        }
    }

    private fun saveTaskList(
        tasks: List<WearTask>
    ) {

        val array = JSONArray()

        tasks.forEach { task ->

            array.put(
                JSONObject().apply {
                    put("id", task.id)
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
                        task.asignadaPor
                    )
                }
            )
        }

        saveTasks(array)
    }

    private fun showTaskNotification(
        task: WearTask
    ) {
        createNotificationChannel()

        val manager =
            getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        val assignedBy =
            if (
                task.asignadaPor.isBlank()
            ) {
                ""
            } else {
                "Asignada por: ${task.asignadaPor}"
            }

        val text =
            listOf(
                task.fecha,
                task.hora,
                assignedBy
            )
                .filter {
                    it.isNotBlank()
                }
                .joinToString(" Ã‚Â· ")

        val notification =
            NotificationCompat
                .Builder(
                    this,
                    CHANNEL_ID
                )
                .setSmallIcon(
                    R.mipmap.ic_launcher
                )
                .setContentTitle(
                    if (
                        task.titulo.isBlank()
                    ) {
                        "Nueva tarea"
                    } else {
                        task.titulo
                    }
                )
                .setContentText(
                    text.ifBlank {
                        "Tienes una nueva tarea asignada"
                    }
                )
                .setPriority(
                    NotificationCompat
                        .PRIORITY_HIGH
                )
                .setAutoCancel(true)
                .build()

        manager.notify(
            task.id.hashCode()
                .takeIf {
                    it != 0
                }
                ?: System.currentTimeMillis()
                    .toInt(),
            notification
        )
    }

    private fun createNotificationChannel() {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val manager =
                getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager

            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    "Tareas MindsAI",
                    NotificationManager
                        .IMPORTANCE_HIGH
                ).apply {
                    description =
                        "Tareas asignadas y recordatorios de MindsAI"
                }

            manager.createNotificationChannel(
                channel
            )
        }
    }

    private fun handleRelevantNotification(
        root: JSONObject,
        defaultTitle: String,
        defaultMessage: String,
        urgent: Boolean
    ) {
        val data =
            root.optJSONObject("data")
                ?: JSONObject()

        val title =
            data.optString("title")
                .ifBlank { defaultTitle }

        val message =
            data.optString("message")
                .ifBlank { defaultMessage }

        WearNotificationStore(this)
            .add(
                type =
                    root.optString("type"),
                title =
                    title,
                message =
                    message,
                timestamp =
                    data.optLong(
                        "timestamp",
                        System.currentTimeMillis()
                    )
            )

        vibrateWatch(urgent)

        showRelevantNotification(
            title = title,
            message = message,
            urgent = urgent
        )
    }


    private fun vibrateWatch(
        urgent: Boolean
    ) {

        val vibrator =
            if (
                Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.S
            ) {

                val manager =
                    getSystemService(
                        VibratorManager::class.java
                    )

                manager.defaultVibrator

            } else {

                @Suppress("DEPRECATION")
                getSystemService(
                    Context.VIBRATOR_SERVICE
                ) as Vibrator
            }

        if (!vibrator.hasVibrator()) {
            return
        }

        if (urgent) {

            val pattern =
                longArrayOf(
                    0,
                    180,
                    100,
                    220
                )

            vibrator.vibrate(
                VibrationEffect.createWaveform(
                    pattern,
                    -1
                )
            )

        } else {

            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    220,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        }
    }


    private fun showRelevantNotification(
        title: String,
        message: String,
        urgent: Boolean
    ) {

        createNotificationChannel()

        val manager =
            getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        val notification =
            NotificationCompat.Builder(
                this,
                CHANNEL_ID
            )
                .setSmallIcon(
                    R.mipmap.ic_launcher
                )
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(
                    NotificationCompat
                        .BigTextStyle()
                        .bigText(message)
                )
                .setPriority(
                    if (urgent) {
                        NotificationCompat.PRIORITY_MAX
                    } else {
                        NotificationCompat.PRIORITY_HIGH
                    }
                )
                .setCategory(
                    NotificationCompat.CATEGORY_REMINDER
                )
                .setAutoCancel(true)
                .build()

        manager.notify(
            System.currentTimeMillis()
                .toInt(),
            notification
        )
    }
}