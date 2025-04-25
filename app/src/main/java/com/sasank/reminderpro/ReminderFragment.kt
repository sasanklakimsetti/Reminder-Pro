package com.sasank.reminderpro

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sasank.reminderpro.models.Reminder
import com.sasank.reminderpro.utils.AlarmUtils
import com.sasank.reminderpro.utils.PreferenceHelper


class ReminderFragment: Fragment(){
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmptyView: TextView
    private lateinit var fabAddReminder: FloatingActionButton

    private lateinit var reminderAdapter: ReminderAdapter
    private lateinit var preferenceHelper: PreferenceHelper
    private lateinit var alarmUtils: AlarmUtils

    private val reminderResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result->
        if(result.resultCode== Activity.RESULT_OK){
            loadReminders()
        }
    }

    private val notificationPermissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){
        isGranted->
        if(!isGranted){
            showNotificationPermissionExplanation()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferenceHelper= PreferenceHelper(requireContext())
        alarmUtils= AlarmUtils(requireContext())

        initViews(view)
        setupListeners()
        loadReminders()

        checkNotificationPermission()

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.S){
            val alarmManager=requireContext().getSystemService(Activity.ALARM_SERVICE) as android.app.AlarmManager
            if(!alarmManager.canScheduleExactAlarms()){
                showExactAlarmPermissionDialog()
            }
        }
    }

    private fun initViews(view: View){
        recyclerView=view.findViewById(R.id.reminderRecyclerView)
        tvEmptyView = view.findViewById(R.id.tvEmptyView)
        fabAddReminder = view.findViewById(R.id.addReminderBtn)

        recyclerView.layoutManager= LinearLayoutManager(requireContext())

        reminderAdapter= ReminderAdapter(
            reminders = emptyList(),
            onReminderClickListener = {reminder->
                openReminderEditor(reminder)
            },
            onDeleteClickListener = { reminder ->
                showDeleteConfirmationDialog(reminder)
            },
            onToggleEnabled = {reminder, isEnabled->
                toggleReminderEnabled(reminder, isEnabled)
            }
        )
        recyclerView.adapter=reminderAdapter
    }

    private fun setupListeners(){
        fabAddReminder.setOnClickListener {
            val intent= Intent(requireContext(), ReminderEditActivity::class.java)
            reminderResultLauncher.launch(intent)
        }
    }

    private fun loadReminders(){
        val reminders=preferenceHelper.getReminders()
        if(reminders.isEmpty()){
            recyclerView.visibility= View.GONE
            tvEmptyView.visibility= View.VISIBLE
        } else{
            recyclerView.visibility= View.VISIBLE
            tvEmptyView.visibility=View.GONE

            val sortedReminders=reminders.sortedBy { it.timeInMillis }
            reminderAdapter.updateReminders(sortedReminders)
        }

        reminders.filter { it.isEnabled }.forEach { reminder ->
            alarmUtils.scheduleAlarm(reminder)
        }
    }

    private fun openReminderEditor(reminder: Reminder){
        val intent= Intent(requireContext(), ReminderEditActivity::class.java).apply {
            putExtra("reminder", reminder)
        }
        reminderResultLauncher.launch(intent)
    }

    private fun showDeleteConfirmationDialog(reminder: Reminder){
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Reminder")
            .setMessage("Are you sure you want to delete this reminder?")
            .setPositiveButton("Delete") { _,_,->
                if(reminder.isEnabled){
                    alarmUtils.cancelAlarm(reminder)
                }
                preferenceHelper.deleteReminder(reminder)
                loadReminders()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun toggleReminderEnabled(reminder: Reminder, isEnabled: Boolean){
        val updatedReminder=reminder.copy(isEnabled=isEnabled)

        if(isEnabled){
            alarmUtils.scheduleAlarm(updatedReminder)
        } else{
            alarmUtils.cancelAlarm(updatedReminder)
        }
        preferenceHelper.updateReminder(updatedReminder)
    }

    private fun checkNotificationPermission(){
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.POST_NOTIFICATIONS
            )!= PackageManager.PERMISSION_GRANTED){
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun showNotificationPermissionExplanation(){
        AlertDialog.Builder(requireContext())
            .setTitle("Notification Permission")
            .setMessage("Notifications are necessary for reminders to work properly. Please enable notifications in app settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Not Now", null)
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent().apply {
            action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = android.net.Uri.fromParts("package", requireContext().packageName, null)
        }
        startActivity(intent)
    }

    private fun showExactAlarmPermissionDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Schedule Exact Alarms")
            .setMessage("This app needs permission to schedule exact alarms for reminders to work properly.")
            .setPositiveButton("Grant Permission") { _, _ ->
                openAlarmSettings()
            }
            .setNegativeButton("Not Now", null)
            .show()
    }

    private fun openAlarmSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent().apply {
                action = android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
            }
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload reminders whenever fragment is resumed
        loadReminders()

        // Re-check alarm permission on resume
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = requireContext().getSystemService(Activity.ALARM_SERVICE) as android.app.AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                // Permission was revoked while app was in background
                showExactAlarmPermissionDialog()
            }
        }
    }
}