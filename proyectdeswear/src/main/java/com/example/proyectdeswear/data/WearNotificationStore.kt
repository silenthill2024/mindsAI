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