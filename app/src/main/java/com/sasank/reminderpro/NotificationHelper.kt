package com.sasank.reminderpro

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationHelper(private val context: Context) {
    companion object {
        const val CHANNEL_ID = "reminder_channel"
        const val NOTIFICATION_ID = 100
    }

    init{
        createNotificationChannel()
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val name="Reminder Notifications"
            val descriptionText="Channel for reminder notifications"
            val importance= NotificationManager.IMPORTANCE_HIGH
            val channel= NotificationChannel(CHANNEL_ID, name, importance).apply {
                description=descriptionText
            }
            val notificationManager=context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(reminderId: Int, title: String, description: String){
        val intent= Intent(context, ReminderFragment::class.java).apply {
            flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("reminder_id", reminderId)
        }

        val pendingIntent= PendingIntent.getActivity(
            context,
            reminderId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder= NotificationCompat.Builder(context, CHANNEL_ID).
                setSmallIcon(R.drawable.event_24px)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(context)){
                notify(reminderId, builder.build())
            }
        } catch (e: SecurityException){
            e.printStackTrace()
        }
    }
}