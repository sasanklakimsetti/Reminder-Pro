package com.sasank.reminderpro.data.database.reminder

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sasank.reminderpro.data.model.Remainder

@Database(entities = [Remainder::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun remainderDao(): RemainderDao
}