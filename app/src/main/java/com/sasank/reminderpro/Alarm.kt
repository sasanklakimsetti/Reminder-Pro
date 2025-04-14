package com.sasank.reminderpro

import android.annotation.SuppressLint
import android.app.TimePickerDialog
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.min

class Alarm : Fragment(){
    private lateinit var alarmRecyclerView: RecyclerView
    private lateinit var addAlarm: FloatingActionButton
    private val alarmList=mutableListOf<AlarmData>()
    private lateinit var alarmAdapter: AlarmAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_alarm,container,false)
        alarmRecyclerView=view.findViewById(R.id.alarmRecyclerView)
        addAlarm=view.findViewById(R.id.addAlarm)

        setupRecyclerView()

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
            val time=String.format("%02d:%02d",h,m)
            val alarm= AlarmData(time, "Alarm", emptyList(), true)
            alarmList.add(alarm)
            alarmAdapter.notifyItemInserted(alarmList.size-1)
        }, hour, minute, false).show()
    }

    private fun toggleAlarm(alarm: AlarmData){
        Toast.makeText(requireContext(), "Alarm ${if (alarm.isEnabled) "On" else "Off"}", Toast.LENGTH_SHORT).show()
    }

    private fun editAlarm(alarm: AlarmData, position: Int) {
        Toast.makeText(requireContext(), "Edit alarm: ${alarm.title}", Toast.LENGTH_SHORT).show()
    }

    private fun deleteAlarm(position: Int) {
        alarmList.removeAt(position)
        alarmAdapter.notifyItemRemoved(position)
    }
}