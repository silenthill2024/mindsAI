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
    version = 7, // Incremented for study tracking and average
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun studyDao(): StudyDao

}
