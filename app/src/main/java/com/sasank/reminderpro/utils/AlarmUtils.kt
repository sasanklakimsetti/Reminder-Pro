package com.sasank.reminderpro.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.sasank.reminderpro.AlarmReceiver
import com.sasank.reminderpro.models.Reminder
import java.util.Calendar

class AlarmUtils(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    fun scheduleAlarm(reminder: Reminder){
        val intent = Intent (context,AlarmReceiver::class.java).apply{
            putExtra(AlarmReceiver.EXTRA_REMINDER_ID, reminder.id)
            putExtra(AlarmReceiver.EXTRA_REMINDER_TITLE, reminder.title)
            putExtra(AlarmReceiver.EXTRA_REMINDER_DESC, reminder.description)
        }
        val pendingIntent = PendingIntent.getBroadcast(context, reminder.id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        if (!reminder.isEnabled){
            cancelAlarm(reminder.id)
            return
        }
        val triggerTime = reminder.timeInMillis
        when{
            reminder.isRepeating && reminder.repeatInterval != Reminder.RepeatInterval.NONE ->
            {
                val intervalMillis = when(reminder.repeatInterval){
                    Reminder.RepeatInterval.DAILY -> AlarmManager.INTERVAL_DAY
                    Reminder.RepeatInterval.WEEKLY -> AlarmManager.INTERVAL_DAY * 7
                    Reminder.RepeatInterval.MONTHLY -> AlarmManager.INTERVAL_DAY * 30
                    else -> 0

                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                    if( alarmManager.canScheduleExactAlarms()){
                        alarmManager.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            triggerTime,
                            intervalMillis,
                            pendingIntent)
                    }
                }
                else{
                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        intervalMillis,
                        pendingIntent)
                }
            }
            else -> {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                    if( alarmManager.canScheduleExactAlarms()){
                        alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            triggerTime,
                            pendingIntent)
                }
                    else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            triggerTime,
                            pendingIntent)
                    }
                }
                else{
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                        )
                    }
            }
        }
    }
    fun cancelAlarm(reminder : Int){
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let{
            alarmManager.cancel(it)
            it.cancel()
        }


    }
    fun getFormattedTime(timeInMillis: Long): String {
        val calendar = Calendar.getInstance().apply {
            this.timeInMillis = timeInMillis
        }

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val amPm = if (hour < 12) "AM" else "PM"
        val hourFormatted = if (hour % 12 == 0) 12 else hour % 12

        return String.format("%d:%02d %s", hourFormatted, minute, amPm)
    }
    fun getFormattedDate(timeInMillis: Long): String {
        val calendar = Calendar.getInstance().apply{
            this.timeInMillis = timeInMillis
        }
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        val monthNames = arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
        return "$day ${monthNames[month]} $year"
    }
}