package com.example.mindsai.local.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"])]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val taskId: Int = 0,
    val userId: Int,
    val title: String,
    val description: String,
    val progress: Float = 0f, // 0.0 to 1.0
    val isCompleted: Boolean = false
)
