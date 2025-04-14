package com.sasank.reminderpro

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class TimeZoneAdapter(
    private val context: Context,
    private val zones: MutableList<String>,
    private val onDelete: (String) -> Unit
) : RecyclerView.Adapter<TimeZoneAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val timeText: TextView = view.findViewById(R.id.timezoneTimeText)
        val deleteIcon: ImageView = view.findViewById(R.id.deleteIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_time_zone, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = zones.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val zoneId = zones[position]
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone(zoneId)
        holder.timeText.text = "${sdf.format(Date())} ($zoneId)"

        holder.deleteIcon.setOnClickListener {
            onDelete(zoneId)
        }
    }

    fun moveItem(from: Int, to: Int) {
        val moved = zones.removeAt(from)
        zones.add(to, moved)
        notifyItemMoved(from, to)
    }
}
