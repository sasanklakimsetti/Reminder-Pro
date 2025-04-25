package com.sasank.reminderpro

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class AlarmReceiver : BroadcastReceiver() {
    companion object{
        const val EXTRA_REMINDER_ID="extra_reminder_id"
        const val EXTRA_REMINDER_TITLE="extra_reminder_title"
        const val EXTRA_REMINDER_DESC="extra_reminder_desc"
    }
    override fun onReceive(context: Context, intent: Intent) {
        val reminderId=intent.getIntExtra(EXTRA_REMINDER_ID,0)
        val title=intent.getStringExtra(EXTRA_REMINDER_TITLE)?:"Reminder"
        val description=intent.getStringExtra(EXTRA_REMINDER_DESC)?:""

        val notificationHelper= NotificationHelper(context)
        notificationHelper.showNotification(reminderId, title, description)
    }

}