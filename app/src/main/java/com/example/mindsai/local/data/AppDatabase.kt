package com.example.mindsai.local.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        SubjectEntity::class,
        QuoteEntity::class,
        TaskEntity::class
    ],
    version = 4, // Incremented version due to new column and index
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun studyDao(): StudyDao

}
