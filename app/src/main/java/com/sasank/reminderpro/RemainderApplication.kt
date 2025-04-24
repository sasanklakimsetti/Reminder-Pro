package com.sasank.reminderpro


import android.app.Application
import android.content.Context
import androidx.room.Room
import com.sasank.reminderpro.data.database.reminder.AppDatabase

class RemainderApplication : Application() {

    companion object {
        private var database: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return database ?: synchronized(this) {
                database ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "remainder_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { database = it }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        getDatabase(this) // Initialize the database on app start
    }
}