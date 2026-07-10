package com.example.mindsai.local.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// This is a NOT RELATED entity. It stands alone.
@Entity(tableName = "quotes")
data class QuoteEntity(
    @PrimaryKey(autoGenerate = true)
    val quoteId: Int = 0,
    val text: String,
    val author: String
)
