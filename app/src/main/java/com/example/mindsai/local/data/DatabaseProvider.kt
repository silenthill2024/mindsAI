package com.example.mindsai.local.data

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    private var INSTANCE: AppDatabase? = null

    fun getDatabase(
        context: Context
    ): AppDatabase {

        return INSTANCE ?: synchronized(this) {

            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "studymind_db"
            )
            .fallbackToDestructiveMigration() // Evita que la app se cierre al cambiar la base de datos
            .build()

            INSTANCE = instance
            instance
        }
    }
}
