package com.sasank.reminderpro.data

import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.constraintlayout.utils.widget.MotionLabel
import androidx.room.Entity
import androidx.room.PrimaryKey
import dalvik.annotation.optimization.CriticalNative

@Entity(tableName = "alarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true) val id: Int=0,
    val hour: Int,
    val minute: Int,
    val isRecurring: Boolean=false,
    val daysOfWeek: Set<Int> = emptySet(),
    var isEnabled: Boolean=true,
    val label: String=""
){
    val timeInMillis: Long
        @RequiresApi(Build.VERSION_CODES.N)
        get(){
            val calendar= Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            if(calendar.timeInMillis<= System.currentTimeMillis()){
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
            return calendar.timeInMillis
        }
}