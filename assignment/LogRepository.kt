package com.macroai.repository

import com.macroai.data.local.LogEntry
import com.macroai.data.local.LogEntryDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LogRepository @Inject constructor(private val logEntryDao: LogEntryDao) {

    fun getAllLogEntries(): Flow<List<LogEntry>> = logEntryDao.getAllLogEntries()

    fun getLogEntryById(id: String): Flow<LogEntry> = logEntryDao.getLogEntryById(id)

    suspend fun insertLogEntry(entry: LogEntry) {
        logEntryDao.insertLogEntry(entry)
    }

    suspend fun updateLogEntry(entry: LogEntry) {
        logEntryDao.updateLogEntry(entry)
    }

    suspend fun deleteLogEntry(entry: LogEntry) {
        logEntryDao.deleteLogEntry(entry)
    }

    suspend fun deleteLogEntryById(id: String) {
        logEntryDao.deleteLogEntryById(id)
    }
}
