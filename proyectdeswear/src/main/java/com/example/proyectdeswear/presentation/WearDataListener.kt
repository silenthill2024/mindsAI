package com.example.proyectdeswear.presentation

import android.util.Log
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.DataMapItem

class WearDataListener(
    private val onTasksReceived: (List<Task>) -> Unit
) : DataClient.OnDataChangedListener {

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED && event.dataItem.uri.path == "/tasks") {
                val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                val tasks = dataMap.getDataMapArrayList("tasks")
                    ?.map(::mapToTask)
                    .orEmpty()

                Log.d("WEAR", "RECIBI TAREAS: ${tasks.map { it.titulo }}")
                onTasksReceived(tasks)
            }
        }
    }

    companion object {
        fun mapToTask(dataMap: DataMap): Task {
            val hasCompletionTime = dataMap.containsKey("completionTime")

            return Task(
                id = dataMap.getString("id").orEmpty(),
                titulo = dataMap.getString("titulo").orEmpty(),
                descripcion = dataMap.getString("descripcion").orEmpty(),
                fecha = dataMap.getString("fecha").orEmpty(),
                hora = dataMap.getString("hora").orEmpty(),
                completado = dataMap.getBoolean("completado"),
                completionTime = if (hasCompletionTime) dataMap.getLong("completionTime") else null,
                documentId = dataMap.getString("documentId").orEmpty()
            )
        }
    }
}
