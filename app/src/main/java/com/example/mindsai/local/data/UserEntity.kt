package com.example.mindsai.local.data
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "users")
@Serializable
data class UserEntity(

    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,

    val nombre:String,

    val correo:String,

    val password:String,

    val description: String = "",
    val level: Int = 1,
    val xp: Int = 0,
    val studyHours: Int = 0,
    val tasksCompleted: Int = 0
)