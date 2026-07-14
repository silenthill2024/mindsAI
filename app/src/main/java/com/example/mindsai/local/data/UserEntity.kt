package com.example.mindsai.local.data
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(

    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,

    val nombre:String,

    val correo:String,

    val password:String,

    val description: String = ""
)