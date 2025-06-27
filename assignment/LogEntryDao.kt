package com.macroai.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LogEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogEntry(entry: LogEntry)

    @Update
    suspend fun updateLogEntry(entry: LogEntry)

    @Delete
    suspend fun deleteLogEntry(entry: LogEntry)

    @Query("SELECT * FROM log_entries ORDER BY timestamp DESC")
    fun getAllLogEntries(): Flow<List<LogEntry>>

    @Query("SELECT * FROM log_entries WHERE id = :id")
    fun getLogEntryById(id: String): Flow<LogEntry>

    @Query("DELETE FROM log_entries WHERE id = :id")
    suspend fun deleteLogEntryById(id: String)
}
