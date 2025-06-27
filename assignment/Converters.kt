package com.macroai.data.local

import androidx.room.TypeConverter
import com.macroai.models.EntryType

class Converters {
    @TypeConverter
    fun fromEntryType(entryType: EntryType): String {
        return entryType.name
    }

    @TypeConverter
    fun toEntryType(entryType: String): EntryType {
        return EntryType.valueOf(entryType)
    }
}
