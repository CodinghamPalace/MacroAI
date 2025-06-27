package com.macroai.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.macroai.models.EntryType

@Entity(tableName = "log_entries")
data class LogEntry(
    @PrimaryKey
    val id: String,
    val name: String,
    val calories: Int,
    val macros: String,
    val type: EntryType,
    val timestamp: Long = System.currentTimeMillis()
)
