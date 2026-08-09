package com.example.proyectdeswear.data

import android.content.Context

class TaskReminderStore(
    context: Context
) {

    companion object {
        private const val PREFS =
            "mindsai_task_reminders"
    }

    private val prefs =
        context.getSharedPreferences(
            PREFS,
            Context.MODE_PRIVATE
        )

    fun postpone(
        taskId: String,
        delayMillis: Long
    ) {
        if (taskId.isBlank()) {
            return
        }

        prefs.edit()
            .putLong(
                taskId,
                System.currentTimeMillis() +
                    delayMillis
            )
            .apply()
    }

    fun getReminderTime(
        taskId: String
    ): Long? {

        if (
            taskId.isBlank() ||
            !prefs.contains(taskId)
        ) {
            return null
        }

        return prefs.getLong(
            taskId,
            0L
        ).takeIf {
            it > 0L
        }
    }

    fun clear(
        taskId: String
    ) {
        prefs.edit()
            .remove(taskId)
            .apply()
    }

    fun isPostponed(
        taskId: String
    ): Boolean {

        val reminder =
            getReminderTime(taskId)
                ?: return false

        return reminder >
            System.currentTimeMillis()
    }
}