package com.sasank.reminderpro

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.DialogTitle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.min

class Alarm : Fragment(){
    private lateinit var alarmRecyclerView: RecyclerView
    private lateinit var addAlarm: FloatingActionButton
    private val alarmList=mutableListOf<AlarmData>()
    private lateinit var alarmAdapter: AlarmAdapter

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_alarm,container,false)
        alarmRecyclerView=view.findViewById(R.id.alarmRecyclerView)
        addAlarm=view.findViewById(R.id.addAlarm)

        setupRecyclerView()
        loadAlarmsFromSharedPreferences()

        addAlarm.setOnClickListener {
            showTimePicker()
        }
        return view
    }

    private fun setupRecyclerView(){
        alarmAdapter= AlarmAdapter(alarmList,
            onSwitchToggle = {alarm->toggleAlarm(alarm)},
            onEdit={alarm, pos->editAlarm(alarm,pos)},
            onDelete = {pos->deleteAlarm(pos)})
        alarmRecyclerView.layoutManager= LinearLayoutManager(requireContext())
        alarmRecyclerView.adapter=alarmAdapter
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showTimePicker(){
        val calendar= Calendar.getInstance()
        val hour=calendar.get(Calendar.HOUR_OF_DAY)
        val minute=calendar.get(Calendar.MINUTE)

        TimePickerDialog(requireContext(),{_, h, m ->
            val alarmTime= Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY,h)
                set(Calendar.MINUTE,m)
                set(Calendar.SECOND,0)
                set(Calendar.MILLISECOND,0)
                if(before(Calendar.getInstance())) add(Calendar.DATE,1)
            }

            val time= SimpleDateFormat("hh:mm a", Locale.getDefault()).format(alarmTime.time)
            val alarmId=generateAlarmId()
            val alarm= AlarmData(alarmId, h, m, time, "Alarm", emptyList(), true)
            alarmList.add(alarm)
            alarmAdapter.notifyItemInserted(alarmList.size-1)

            saveAlarmToSharedPreferences(alarm)
            scheduleAlarm(alarmTime.timeInMillis, alarm.title)
        }, hour, minute, false).show()
    }

    private fun generateAlarmId(): Int{
        return if(alarmList.isNotEmpty()){
            alarmList.maxOf { it.id }+1
        } else 1
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleAlarm(triggerTime: Long, title: String){
        val intent= Intent(requireContext(), AlarmReceiver::class.java).apply {
            putExtra("alarm_title", title)
        }

        val pendingIntent= PendingIntent.getBroadcast(
            requireContext(),
            title.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

//        val alarmManager=requireContext().getSystemService(android.content.Context.ALARM_SERVICE) as android.app.AlarmManager
//        alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
//
//        Toast.makeText(requireContext(), "Alarm set for $title", Toast.LENGTH_SHORT).show()
    }

    private fun toggleAlarm(alarm: AlarmData){
        Toast.makeText(requireContext(), "Alarm ${if (alarm.isEnabled) "On" else "Off"}", Toast.LENGTH_SHORT).show()
        saveAlarmToSharedPreferences(alarm)
    }

    private fun editAlarm(alarm: AlarmData, position: Int) {
        Toast.makeText(requireContext(), "Edit alarm: ${alarm.title}", Toast.LENGTH_SHORT).show()
    }

    private fun deleteAlarm(position: Int) {
        val alarm=alarmList.removeAt(position)
        alarmAdapter.notifyItemRemoved(position)
        alarmAdapter.notifyItemRemoved(position)
        deleteAlarmFromSharedPreferences(alarm.id)
    }

    private fun saveAlarmToSharedPreferences(alarm: AlarmData){
        val sharedPreferences=requireContext().getSharedPreferences("alarms", Context.MODE_PRIVATE)
        val editor=sharedPreferences.edit()
        val alarmJson= Gson().toJson(alarm)
        editor.putString("alarm_${alarm.id}", alarmJson)
        editor.apply()
    }

    private fun deleteAlarmFromSharedPreferences(alarmId: Int){
        val sharedPreferences=requireContext().getSharedPreferences("alarms", Context.MODE_PRIVATE)
        val editor=sharedPreferences.edit()
        editor.remove("alarm_$alarmId")
        editor.apply()
    }

    private fun loadAlarmsFromSharedPreferences(){
        val sharedPreferences=requireContext().getSharedPreferences("alarms", Context.MODE_PRIVATE)
        val allAlarms=sharedPreferences.all

        allAlarms.forEach { (key, value) ->
            if(key.startsWith("alarm_")){
                val alarmJson=value as String
                val alarm= Gson().fromJson(alarmJson, AlarmData::class.java)
                alarmList.add(alarm)
            }
        }

        alarmAdapter.notifyDataSetChanged()
    }
}