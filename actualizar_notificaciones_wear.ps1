# ============================================================
# MindsAI - Actualización completa de notificaciones Wear OS
# ============================================================

$ErrorActionPreference = "Stop"

Write-Host ""
Write-Host "==============================================" -ForegroundColor Cyan
Write-Host " MINDSAI - NOTIFICACIONES WEAR OS" -ForegroundColor Cyan
Write-Host "==============================================" -ForegroundColor Cyan
Write-Host ""

# ------------------------------------------------------------
# 1. RUTAS
# ------------------------------------------------------------

$project = "C:\Users\OBED\AndroidStudioProjects\mindsAI"

$wearRoot = Join-Path `
    $project `
    "proyectdeswear"

$listener = Join-Path `
    $wearRoot `
    "src\main\java\com\example\proyectdeswear\presentation\WearMessageListener.kt"

$notificationFile = Join-Path `
    $wearRoot `
    "src\main\java\com\example\proyectdeswear\presentation\notificationsv3\NotificationCenterV3.kt"

$store = Join-Path `
    $wearRoot `
    "src\main\java\com\example\proyectdeswear\data\WearNotificationStore.kt"

$manifest = Join-Path `
    $wearRoot `
    "src\main\AndroidManifest.xml"

$apk = Join-Path `
    $wearRoot `
    "build\outputs\apk\debug\proyectdeswear-debug.apk"

$adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"

$watch = "192.168.100.49:40481"

$utf8 = New-Object System.Text.UTF8Encoding($false)

Set-Location $project

# ------------------------------------------------------------
# 2. VALIDAR ARCHIVOS
# ------------------------------------------------------------

Write-Host "Validando archivos..." -ForegroundColor Yellow

$required = @(
    $listener,
    $notificationFile,
    $manifest
)

foreach ($file in $required) {

    if (-not (Test-Path $file)) {

        Write-Host ""
        Write-Host "ERROR: No existe:" -ForegroundColor Red
        Write-Host $file -ForegroundColor Red

        exit 1
    }
}

Write-Host "OK - archivos encontrados" -ForegroundColor Green

# ------------------------------------------------------------
# 3. RESPALDOS
# ------------------------------------------------------------

$backupDir = Join-Path `
    $project `
    ("backup_wear_notifications_" + (Get-Date -Format "yyyyMMdd_HHmmss"))

New-Item `
    -ItemType Directory `
    -Path $backupDir `
    -Force |
    Out-Null

Copy-Item `
    $listener `
    (Join-Path $backupDir "WearMessageListener.kt") `
    -Force

Copy-Item `
    $notificationFile `
    (Join-Path $backupDir "NotificationCenterV3.kt") `
    -Force

Copy-Item `
    $manifest `
    (Join-Path $backupDir "AndroidManifest.xml") `
    -Force

if (Test-Path $store) {

    Copy-Item `
        $store `
        (Join-Path $backupDir "WearNotificationStore.kt") `
        -Force
}

Write-Host ""
Write-Host "Backup creado:" -ForegroundColor Green
Write-Host $backupDir

# ------------------------------------------------------------
# 4. MANIFEST - VIBRACION
# ------------------------------------------------------------

Write-Host ""
Write-Host "Configurando permiso de vibración..." -ForegroundColor Yellow

$manifestContent = Get-Content $manifest -Raw

if (
    $manifestContent -notmatch
    'android\.permission\.VIBRATE'
) {

    $manifestContent =
        $manifestContent.Replace(
            '<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />',
@'
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.VIBRATE" />
'@
        )

    [System.IO.File]::WriteAllText(
        $manifest,
        $manifestContent,
        $utf8
    )

    Write-Host "Permiso VIBRATE agregado" -ForegroundColor Green

} else {

    Write-Host "Permiso VIBRATE ya existe" -ForegroundColor DarkGreen
}

# ------------------------------------------------------------
# 5. CREAR WearNotificationStore.kt
# ------------------------------------------------------------

Write-Host ""
Write-Host "Creando almacén persistente de notificaciones..." -ForegroundColor Yellow

$storeContent = @'
package com.example.proyectdeswear.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class WearNotificationItem(
    val id: String,
    val type: String,
    val title: String,
    val message: String,
    val timestamp: Long
)

class WearNotificationStore(
    context: Context
) {

    companion object {
        private const val PREFS =
            "mindsai_wear_notifications"

        private const val KEY =
            "notifications_json"

        private const val MAX_ITEMS =
            30
    }

    private val prefs =
        context.getSharedPreferences(
            PREFS,
            Context.MODE_PRIVATE
        )


    fun add(
        type: String,
        title: String,
        message: String,
        timestamp: Long =
            System.currentTimeMillis()
    ) {

        val current =
            getAll()
                .toMutableList()

        current.add(
            0,
            WearNotificationItem(
                id =
                    "${timestamp}_${type}",
                type =
                    type,
                title =
                    title,
                message =
                    message,
                timestamp =
                    timestamp
            )
        )

        save(
            current.take(
                MAX_ITEMS
            )
        )
    }


    fun getAll():
        List<WearNotificationItem> {

        val raw =
            prefs.getString(
                KEY,
                "[]"
            ) ?: "[]"

        return try {

            val array =
                JSONArray(raw)

            buildList {

                for (
                    index in
                    0 until array.length()
                ) {

                    val item =
                        array.optJSONObject(
                            index
                        ) ?: continue

                    add(
                        WearNotificationItem(
                            id =
                                item.optString(
                                    "id"
                                ),
                            type =
                                item.optString(
                                    "type"
                                ),
                            title =
                                item.optString(
                                    "title"
                                ),
                            message =
                                item.optString(
                                    "message"
                                ),
                            timestamp =
                                item.optLong(
                                    "timestamp"
                                )
                        )
                    )
                }
            }

        } catch (_: Exception) {

            emptyList()
        }
    }


    fun clear() {

        prefs.edit()
            .remove(KEY)
            .apply()
    }


    private fun save(
        items:
            List<WearNotificationItem>
    ) {

        val array =
            JSONArray()

        items.forEach { item ->

            array.put(
                JSONObject().apply {

                    put(
                        "id",
                        item.id
                    )

                    put(
                        "type",
                        item.type
                    )

                    put(
                        "title",
                        item.title
                    )

                    put(
                        "message",
                        item.message
                    )

                    put(
                        "timestamp",
                        item.timestamp
                    )
                }
            )
        }

        prefs.edit()
            .putString(
                KEY,
                array.toString()
            )
            .apply()
    }
}
'@

[System.IO.File]::WriteAllText(
    $store,
    $storeContent,
    $utf8
)

Write-Host "WearNotificationStore.kt creado" -ForegroundColor Green

# ------------------------------------------------------------
# 6. ACTUALIZAR WearMessageListener.kt
# ------------------------------------------------------------

Write-Host ""
Write-Host "Actualizando WearMessageListener..." -ForegroundColor Yellow

$c = Get-Content $listener -Raw

# Imports vibración
if (
    $c -notmatch
    'import android\.os\.VibrationEffect'
) {

    $c =
        $c.Replace(
            'import android.os.Build',
@'
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
'@
        )
}

# Import store
if (
    $c -notmatch
    'WearNotificationStore'
) {

    $c =
        $c.Replace(
            'import com.example.proyectdeswear.data.WearSessionStore',
@'
import com.example.proyectdeswear.data.WearSessionStore
import com.example.proyectdeswear.data.WearNotificationStore
'@
        )
}

# Eventos nuevos
if (
    $c -notmatch
    '"BLOG_COMMENT"\s*->'
) {

    $target = @'
                "TASK_UPDATED" -> {
                    handleTaskUpdated(root)
                }
'@

    $replacement = @'
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
                        defaultMessage = "Se agregó nuevo material de apoyo",
                        urgent = false
                    )
                }

                "TASK_DUE" -> {
                    handleRelevantNotification(
                        root = root,
                        defaultTitle = "Tarea por vencer",
                        defaultMessage = "Tienes una tarea próxima a vencer",
                        urgent = true
                    )
                }
'@

    $c =
        $c.Replace(
            $target,
            $replacement
        )
}

# Añadir historial a TASK_ASSIGNED si todavía no está
if (
    $c -notmatch
    'type\s*=\s*"TASK_ASSIGNED"'
) {

    $target = @'
        showTaskNotification(task)
'@

    $replacement = @'
        WearNotificationStore(this)
            .add(
                type =
                    "TASK_ASSIGNED",
                title =
                    "Nueva tarea",
                message =
                    task.titulo
            )

        vibrateWatch(
            urgent = true
        )

        showTaskNotification(task)
'@

    $c =
        $c.Replace(
            $target,
            $replacement
        )
}

# Funciones nuevas
if (
    $c -notmatch
    'private fun handleRelevantNotification'
) {

$functions = @'


    private fun handleRelevantNotification(
        root: JSONObject,
        defaultTitle: String,
        defaultMessage: String,
        urgent: Boolean
    ) {

        val data =
            root.optJSONObject(
                "data"
            ) ?: JSONObject()

        val title =
            data.optString(
                "title"
            ).ifBlank {
                defaultTitle
            }

        val message =
            data.optString(
                "message"
            ).ifBlank {
                defaultMessage
            }

        val timestamp =
            data.optLong(
                "timestamp",
                System.currentTimeMillis()
            )

        WearNotificationStore(this)
            .add(
                type =
                    root.optString(
                        "type"
                    ),
                title =
                    title,
                message =
                    message,
                timestamp =
                    timestamp
            )

        vibrateWatch(
            urgent
        )

        showRelevantNotification(
            title =
                title,
            message =
                message,
            urgent =
                urgent
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

        if (
            !vibrator.hasVibrator()
        ) {
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
                VibrationEffect
                    .createWaveform(
                        pattern,
                        -1
                    )
            )

        } else {

            vibrator.vibrate(
                VibrationEffect
                    .createOneShot(
                        220,
                        VibrationEffect
                            .DEFAULT_AMPLITUDE
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
            NotificationCompat
                .Builder(
                    this,
                    CHANNEL_ID
                )
                .setSmallIcon(
                    R.mipmap.ic_launcher
                )
                .setContentTitle(
                    title
                )
                .setContentText(
                    message
                )
                .setStyle(
                    NotificationCompat
                        .BigTextStyle()
                        .bigText(
                            message
                        )
                )
                .setPriority(
                    if (urgent) {
                        NotificationCompat
                            .PRIORITY_MAX
                    } else {
                        NotificationCompat
                            .PRIORITY_HIGH
                    }
                )
                .setCategory(
                    NotificationCompat
                        .CATEGORY_REMINDER
                )
                .setAutoCancel(
                    true
                )
                .build()

        manager.notify(
            System.currentTimeMillis()
                .toInt(),
            notification
        )
    }

'@

    $lastBrace =
        $c.LastIndexOf(
            "}"
        )

    if (
        $lastBrace -lt 0
    ) {
        throw "No se encontró el cierre de WearMessageListener"
    }

    $c =
        $c.Insert(
            $lastBrace,
            $functions
        )
}

[System.IO.File]::WriteAllText(
    $listener,
    $c,
    $utf8
)

Write-Host "WearMessageListener actualizado" -ForegroundColor Green

# ------------------------------------------------------------
# 7. ACTUALIZAR NotificationCenterV3.kt
# ------------------------------------------------------------

Write-Host ""
Write-Host "Actualizando NotificationCenterV3..." -ForegroundColor Yellow

$c = Get-Content $notificationFile -Raw

# Imports
if (
    $c -notmatch
    'androidx\.compose\.ui\.platform\.LocalContext'
) {

    $c =
        $c.Replace(
            'import androidx.compose.ui.Modifier',
@'
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
'@
        )
}

if (
    $c -notmatch
    'com\.example\.proyectdeswear\.data\.WearNotificationStore'
) {

    $c =
        $c.Replace(
            'import com.example.proyectdeswear.presentation.Task',
@'
import com.example.proyectdeswear.presentation.Task
import com.example.proyectdeswear.data.WearNotificationStore
'@
        )
}

if (
    $c -notmatch
    'import androidx\.compose\.runtime\.remember'
) {

    $c =
        $c.Replace(
            'import androidx.compose.runtime.Composable',
@'
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
'@
        )
}

# Reemplazar lista de notificaciones
$oldNotifications = @'
    val notifications =
        buildTaskNotifications(tasks)
'@

$newNotifications = @'
    val context =
        LocalContext.current

    val storedNotifications =
        remember {
            WearNotificationStore(
                context.applicationContext
            ).getAll()
        }

    val eventNotifications =
        storedNotifications.map { item ->

            RelevantNotification(
                id =
                    item.id,

                type =
                    when (
                        item.type
                    ) {

                        "BLOG_COMMENT" ->
                            RelevantNotificationType.COMMENT

                        "NEW_MATERIAL" ->
                            RelevantNotificationType.MATERIAL

                        else ->
                            RelevantNotificationType.TASK_DUE
                    },

                title =
                    item.title,

                message =
                    item.message,

                timeLabel =
                    formatRelativeTime(
                        item.timestamp
                    )
            )
        }

    val taskNotifications =
        buildTaskNotifications(
            tasks
        )

    val notifications =
        (
            eventNotifications +
                taskNotifications
        )
            .distinctBy {
                it.id
            }
'@

if (
    $c.Contains(
        $oldNotifications
    )
) {

    $c =
        $c.Replace(
            $oldNotifications,
            $newNotifications
        )

    Write-Host "Historial conectado al centro visual" -ForegroundColor Green

} elseif (
    $c -match
    'WearNotificationStore'
) {

    Write-Host "El centro ya parece modificado" -ForegroundColor DarkGreen

} else {

    Write-Host ""
    Write-Host "ADVERTENCIA:" -ForegroundColor Yellow
    Write-Host "No se encontró el bloque exacto de 'val notifications'." -ForegroundColor Yellow
    Write-Host "No se sobrescribió esa parte para evitar romper el archivo." -ForegroundColor Yellow
}

# formatRelativeTime
if (
    $c -notmatch
    'private fun formatRelativeTime'
) {

$relativeTime = @'


private fun formatRelativeTime(
    timestamp: Long
): String {

    val difference =
        (
            System.currentTimeMillis() -
                timestamp
        ).coerceAtLeast(
            0L
        )

    val minute =
        60_000L

    val hour =
        60L *
            minute

    val day =
        24L *
            hour

    return when {

        difference <
            minute ->
            "Ahora"

        difference <
            hour -> {

            val value =
                difference /
                    minute

            "Hace $value min"
        }

        difference <
            day -> {

            val value =
                difference /
                    hour

            "Hace $value h"
        }

        else -> {

            val value =
                difference /
                    day

            "Hace $value d"
        }
    }
}

'@

    $lastBrace =
        $c.LastIndexOf(
            "}"
        )

    if (
        $lastBrace -lt 0
    ) {
        throw "No se encontró el cierre de NotificationCenterV3"
    }

    $c =
        $c.Insert(
            $lastBrace,
            $relativeTime
        )
}

[System.IO.File]::WriteAllText(
    $notificationFile,
    $c,
    $utf8
)

Write-Host "NotificationCenterV3 actualizado" -ForegroundColor Green

# ------------------------------------------------------------
# 8. VERIFICACION
# ------------------------------------------------------------

Write-Host ""
Write-Host "==============================================" -ForegroundColor Cyan
Write-Host " VERIFICACION" -ForegroundColor Cyan
Write-Host "==============================================" -ForegroundColor Cyan

Write-Host ""

Select-String `
    $listener `
    -Pattern `
    "BLOG_COMMENT|NEW_MATERIAL|TASK_DUE|WearNotificationStore|vibrateWatch" |
    Select-Object LineNumber, Line

Write-Host ""

Select-String `
    $notificationFile `
    -Pattern `
    "WearNotificationStore|eventNotifications|formatRelativeTime" |
    Select-Object LineNumber, Line

# ------------------------------------------------------------
# 9. COMPILAR
# ------------------------------------------------------------

Write-Host ""
Write-Host "==============================================" -ForegroundColor Cyan
Write-Host " COMPILANDO SMARTWATCH" -ForegroundColor Cyan
Write-Host "==============================================" -ForegroundColor Cyan
Write-Host ""

& ".\gradlew.bat" `
    ":proyectdeswear:assembleDebug"

if (
    $LASTEXITCODE -ne 0
) {

    Write-Host ""
    Write-Host "BUILD FAILED" -ForegroundColor Red
    Write-Host ""
    Write-Host "Se conservaron los respaldos en:" -ForegroundColor Yellow
    Write-Host $backupDir

    exit $LASTEXITCODE
}

Write-Host ""
Write-Host "BUILD SUCCESSFUL" -ForegroundColor Green

# ------------------------------------------------------------
# 10. INSTALAR EN RELOJ
# ------------------------------------------------------------

if (
    -not (Test-Path $adb)
) {

    Write-Host ""
    Write-Host "ADB no encontrado:" -ForegroundColor Red
    Write-Host $adb
    exit 1
}

if (
    -not (Test-Path $apk)
) {

    Write-Host ""
    Write-Host "APK no encontrada:" -ForegroundColor Red
    Write-Host $apk
    exit 1
}

Write-Host ""
Write-Host "Buscando smartwatch..." -ForegroundColor Yellow

$devices =
    & $adb devices

if (
    $devices -notmatch
    [regex]::Escape(
        $watch
    )
) {

    Write-Host "Intentando conectar al reloj..." -ForegroundColor Yellow

    & $adb connect $watch |
        Out-Host
}

Write-Host ""
Write-Host "Instalando APK Wear..." -ForegroundColor Yellow

& $adb `
    -s $watch `
    install `
    -r `
    $apk

if (
    $LASTEXITCODE -ne 0
) {

    Write-Host ""
    Write-Host "La compilación funcionó, pero la instalación falló." -ForegroundColor Red
    Write-Host "Comprueba la IP:puerto del smartwatch." -ForegroundColor Yellow

    exit $LASTEXITCODE
}

# ------------------------------------------------------------
# 11. ABRIR APP
# ------------------------------------------------------------

Write-Host ""
Write-Host "Abriendo MindsAI en el smartwatch..." -ForegroundColor Yellow

& $adb `
    -s $watch `
    shell `
    monkey `
    -p com.example.proyectodesdisint `
    -c android.intent.category.LAUNCHER `
    1 |
    Out-Null

Write-Host ""
Write-Host "==============================================" -ForegroundColor Green
Write-Host " PROCESO TERMINADO" -ForegroundColor Green
Write-Host "==============================================" -ForegroundColor Green
Write-Host ""

Write-Host "Ahora el smartwatch soporta:" -ForegroundColor Cyan
Write-Host " - Comentarios de Comunidad"
Write-Host " - Material nuevo"
Write-Host " - Tareas importantes"
Write-Host " - Vibración"
Write-Host " - Notificaciones Wear OS"
Write-Host " - Historial persistente"
Write-Host ""

Write-Host "Backup:" -ForegroundColor Cyan
Write-Host $backupDir
Write-Host ""

