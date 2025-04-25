package com.sasank.reminderpro.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sasank.reminderpro.models.Reminder

class PreferenceHelper(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREF_NAME, Context.MODE_PRIVATE
    )

    companion object {
        private const val PREF_NAME = "REMINDER_APP_PREFERENCES"
        private const val KEY_REMINDERS = "KEY_REMINDERS"
        private const val KEY_LAST_ID = "KEY_LAST_ID"
    }

    fun saveReminders(reminders: List<Reminder>) {
        val gson = Gson()
        val json = gson.toJson(reminders)
        sharedPreferences.edit().putString(KEY_REMINDERS, json).apply()
    }

    fun getReminders(): List<Reminder> {
        val gson = Gson()
        val json = sharedPreferences.getString(KEY_REMINDERS, null)
        return if (json != null) {
            val type = object : TypeToken<List<Reminder>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun getNextId(): Int {
        val lastId = sharedPreferences.getInt(KEY_LAST_ID, 0)
        val nextId = lastId + 1
        sharedPreferences.edit().putInt(KEY_LAST_ID, nextId).apply()
        return nextId
    }

    fun saveReminder(reminder: Reminder) {
        val reminders = getReminders().toMutableList()
        val existingIndex = reminders.indexOfFirst { it.id == reminder.id }

        if (existingIndex != -1) {
            reminders[existingIndex] = reminder
        } else {
            reminders.add(reminder)
        }

        saveReminders(reminders)
    }

    // Method for updating a reminder (used in ReminderFragment)
    fun updateReminder(reminder: Reminder) {
        saveReminder(reminder)
    }

    // Method for deleting a reminder by ID
    fun deleteReminder(id: Int) {
        val reminders = getReminders().toMutableList()
        // Use a safer way to remove items that works on all API levels
        val iterator = reminders.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (item.id == id) {
                iterator.remove()
                break
            }
        }
        saveReminders(reminders)
    }

    // Overloaded method for deleting a reminder by Reminder object
    fun deleteReminder(reminder: Reminder) {
        deleteReminder(reminder.id)
    }
}