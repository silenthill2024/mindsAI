package com.example.mindsai.local.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        SubjectEntity::class,
        QuoteEntity::class
    ],
    version = 2, // Version increased because we added new tables
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun studyDao(): StudyDao

}
