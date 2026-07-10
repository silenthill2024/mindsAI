package com.example.mindsai.local.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// This is a RELATIONAL entity. It is linked to a User.
@Entity(
    tableName = "subjects",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true)
    val subjectId: Int = 0,
    val userId: Int, // Foreign Key
    val name: String,
    val color: String
)
