package com.sasank.reminderpro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TimeZoneAdapter(
    private val timezones: MutableList<String>,
    private val selectedItems: MutableSet<String>
) : RecyclerView.Adapter<TimeZoneAdapter.TimezoneViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimezoneViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_time_zone, parent, false)
        return TimezoneViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimezoneViewHolder, position: Int) {
        val zoneId = timezones[position]
        holder.bind(zoneId)
    }

    override fun getItemCount(): Int = timezones.size

    fun removeSelected() {
        timezones.removeAll(selectedItems)
        selectedItems.clear()
        notifyDataSetChanged()
    }

    inner class TimezoneViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView = itemView.findViewById<TextView>(R.id.timezoneTimeText)
        private val checkBox = itemView.findViewById<CheckBox>(R.id.timezoneCheckbox)

        fun bind(zoneId: String) {
            textView.text = zoneId
            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = selectedItems.contains(zoneId)

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) selectedItems.add(zoneId)
                else selectedItems.remove(zoneId)
            }
        }
    }
}
