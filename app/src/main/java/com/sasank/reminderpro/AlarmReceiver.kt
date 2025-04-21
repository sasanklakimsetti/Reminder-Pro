package com.sasank.reminderpro

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val title= intent?.getStringExtra("alarm_title")?:"Alarm"
        val alarmId=intent?.getIntExtra("alarm_id", System.currentTimeMillis().toInt())
        val notificationManager= context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId="alarm_channel"

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(
                    channelId,
                    "Alarm Notifications",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description="Channel for alarm notification"
                    enableLights(true)
                    enableVibration(true)
                    setSound(
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .build()
                    )
                }
                notificationManager.createNotificationChannel(channel)
            }
        }

        val notification= NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText("Hey babu, lev")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}