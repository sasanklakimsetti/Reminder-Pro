package com.sasank.reminderpro

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.sasank.reminderpro.models.Reminder
import com.sasank.reminderpro.utils.PreferenceHelper
import kotlinx.coroutines.internal.OpDescriptor
import java.util.Calendar

class ReminderEditActivity : AppCompatActivity() {
    private lateinit var etTitle: EditText
    private lateinit var etDescription: EditText
    private lateinit var btnSetDate: Button
    private lateinit var tvSelectedDate: TextView
    private lateinit var btnSetTime: Button
    private lateinit var cbRepeat: CheckBox
    private lateinit var spinnerRepeatInterval: Spinner
    private lateinit var btnSave: Button
    private lateinit var preferenceHelper: PreferenceHelper
    private var reminderCalendar = Calendar.getInstance()
    private var isEditing = false
    private var currentReminderId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder_edit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        preferenceHelper = PreferenceHelper(this)
        initViews()
        setupListeners()
        if (intent.hasExtra("reminder")) {
            val reminder =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra("reminder", Reminder::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getSerializableExtra("reminder") as Reminder
                }
            reminder?.let {
                setupForEdit(it)
            }

        }
        updateDateTimeDisplay()
    }

    private fun initViews() {
        etTitle = findViewById(R.id.etTitle)
        etDescription = findViewById(R.id.etDescription)
        btnSetDate = findViewById(R.id.btnSetDate)
        tvSelectedDate = findViewById(R.id.tvSelectedDate)
        btnSetTime = findViewById(R.id.btnSetTime)
        cbRepeat = findViewById(R.id.cbRepeat)
        spinnerRepeatInterval = findViewById(R.id.spinnerRepeatInterval)
        btnSave = findViewById(R.id.btnSave)

        val repeatOptions = arrayOf("Daily", "Weekly", "Monthly")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, repeatOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRepeatInterval.adapter = adapter

        spinnerRepeatInterval.visibility = View.GONE
    }

    private fun setupListeners() {
        btnSetDate.setOnClickListener {
            showDatePicker()
        }
        btnSetTime.setOnClickListener {
            showTimePicker()
        }
        cbRepeat.setOnCheckedChangeListener { _, isChecked ->
            spinnerRepeatInterval.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
        btnSave.setOnClickListener {
            saveReminder()
        }
    }

    private fun setupForEdit(reminder: Reminder) {
        isEditing = true
        currentReminderId = reminder.id
        etTitle.setText(reminder.title)
        etDescription.setText(reminder.description)
        reminderCalendar.timeInMillis = reminder.timeInMillis
        cbRepeat.isChecked = reminder.isRepeating
        if (reminder.isRepeating) {
            val position = when (reminder.repeatInterval) {
                Reminder.RepeatInterval.DAILY -> 0
                Reminder.RepeatInterval.WEEKLY -> 1
                Reminder.RepeatInterval.MONTHLY -> 2
                else -> 0

            }
            spinnerRepeatInterval.setSelection(position)
            spinnerRepeatInterval.visibility = View.VISIBLE

        }
        supportActionBar?.title = "Edit Remainder"

    }

    private fun showDatePicker() {
        val year = reminderCalendar.get(Calendar.YEAR)
        val month = reminderCalendar.get(Calendar.MONTH)
        val day = reminderCalendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            reminderCalendar.set(Calendar.YEAR, selectedYear)
            reminderCalendar.set(Calendar.MONTH, selectedMonth)
            reminderCalendar.set(Calendar.DAY_OF_MONTH, selectedDay)
            updateDateTimeDisplay()
        }, year, month, day).show()
    }
    private fun showTimePicker(){
        val hour = reminderCalendar.get(Calendar.HOUR_OF_DAY)
        val minute = reminderCalendar.get(Calendar.MINUTE)
        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            reminderCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
            reminderCalendar.set(Calendar.MINUTE, selectedMinute)
            updateDateTimeDisplay()
        }, hour, minute, false).show()
    }
    private fun updateDateTimeDisplay(){
        val day = reminderCalendar.get(Calendar.DAY_OF_MONTH)
        val month = reminderCalendar.get(Calendar.MONTH)
        val year = reminderCalendar.get(Calendar.YEAR)
        val monthNames = arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
        tvSelectedDate.text = "$day ${monthNames[month]} $year"
        val hour = reminderCalendar.get(Calendar.HOUR_OF_DAY)
        val minute = reminderCalendar.get(Calendar.MINUTE)
        val amPm = if (hour < 12) "AM" else "PM"
        val hourFormatted = if (hour % 12 == 0) 12 else hour % 12
        tvSelectedDate.text = String.format("%d:%02d %s", hourFormatted, minute, amPm)



    }

    private fun saveReminder() {
        val title = etTitle.text.toString().trim()
        if (title.isEmpty()) {
            etTitle.error = "Title is required"
            return
        }

        val description = etDescription.text.toString().trim()
        val timeInMillis = reminderCalendar.timeInMillis

        // Check if time is in the past
        if (timeInMillis < System.currentTimeMillis()) {
            showMessage("Cannot set reminder for past time")
            return
        }

        val isRepeating = cbRepeat.isChecked
        val repeatInterval = when {
            !isRepeating -> Reminder.RepeatInterval.NONE
            else -> when (spinnerRepeatInterval.selectedItemPosition) {
                0 -> Reminder.RepeatInterval.DAILY
                1 -> Reminder.RepeatInterval.WEEKLY
                2 -> Reminder.RepeatInterval.MONTHLY
                else -> Reminder.RepeatInterval.NONE
            }
        }

        val reminderId = if (isEditing) currentReminderId else preferenceHelper.getNextId()

        val reminder = Reminder(
            id = reminderId,
            title = title,
            description = description,
            timeInMillis = timeInMillis,
            isRepeating = isRepeating,
            repeatInterval = repeatInterval,
            isEnabled = true
        )

        preferenceHelper.saveReminder(reminder)

        // Return result
        val resultIntent = Intent().apply {
            putExtra("reminder_updated", true)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun showMessage(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}