package com.sasank.reminderpro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AlarmAdapter(
    private val alarms: MutableList<AlarmData>,
    private val onSwitchToggle: (AlarmData)-> Unit,
    private val onEdit:(AlarmData, Int)-> Unit,
    private val onDelete:(Int)-> Unit
) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>(){

    inner class AlarmViewHolder(view: View): RecyclerView.ViewHolder(view){
        val alarmTime: TextView=view.findViewById(R.id.alarmTime)
        val alarmTitle: TextView=view.findViewById(R.id.alarmTitle)
        val alarmSwitch: Switch=view.findViewById(R.id.alarmSwitch)

        init{
            view.setOnClickListener {
                onEdit(alarms[adapterPosition],adapterPosition)
            }
            view.setOnLongClickListener {
                onDelete(adapterPosition)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_alarm,parent,false)
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm=alarms[position]
        holder.alarmTime.text=alarm.time
        holder.alarmTitle.text=alarm.title
        holder.alarmSwitch.isChecked=alarm.isEnabled

        holder.alarmSwitch.setOnCheckedChangeListener { _, isChecked ->
            alarms[position].isEnabled=isChecked
            onSwitchToggle(alarms[position])
        }
    }

    override fun getItemCount(): Int = alarms.size
}