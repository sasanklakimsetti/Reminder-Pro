package com.sasank.reminderpro

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sasank.reminderpro.utils.AlarmUtils
import com.sasank.reminderpro.utils.PreferenceHelper


class ReminderFragment: Fragment(){
    private lateinit var recycleView: RecyclerView
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
        recycleView=view.findViewById(R.id.recyclerView)
        tvEmptyView = view.findViewById(R.id.tvEmptyView)
        fabAddReminder = view.findViewById(R.id.fabAddReminder)
    }
}