package com.sasank.reminderpro.usables.alarm

import android.R
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sasank.reminderpro.daos.AlarmDao
import com.sasank.reminderpro.databases.AlarmDatabase
import com.sasank.reminderpro.entities.AlarmEntity
import kotlinx.coroutines.launch

class Testing : AppCompatActivity() {
    private lateinit var alarmDao: AlarmDao

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(com.sasank.reminderpro.R.layout.activity_main)

        val db= AlarmDatabase.getDatabase(this)
        alarmDao=db.alarmDao()

        lifecycleScope.launch {
            val alarm= AlarmEntity(
                id=1,
                label = "Morning Alarm",
                hour=7,
                minute=0,
                isEnabled = true,
                timeInMillis = System.currentTimeMillis()+60000
            )
            alarmDao.insertAlarm(alarm)
        }

        lifecycleScope.launch {
            alarmDao.getAllAlarms().collect { alarms->
                alarms.forEach {
                    Log.d("Alarm", "Alarm: ${it.label} at ${it.hour}:${it.minute} | Active: ${it.isEnabled}")
                }
            }
        }
    }
}