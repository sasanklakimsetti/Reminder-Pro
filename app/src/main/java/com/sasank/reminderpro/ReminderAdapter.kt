package com.sasank.reminderpro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.sasank.reminderpro.models.Reminder
import com.sasank.reminderpro.utils.AlarmUtils

class ReminderAdapter(
    private var reminders: List<Reminder>,
    private val onReminderClickListener: (Reminder)-> Unit,
    private val onDeleteClickListener: (Reminder)-> Unit,
    private val onToggleEnabled: (Reminder, Boolean) -> Unit
) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {
    private lateinit var alarmUtils: AlarmUtils
    inner class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val titleTextView: TextView = itemView.findViewById(R.id.tvReminderTitle)
        val timeTextView: TextView = itemView.findViewById(R.id.tvReminderTime)
        val dateTextView: TextView = itemView.findViewById(R.id.tvReminderDate)
        val repeatTextView: TextView = itemView.findViewById(R.id.tvRepeatStatus)
        val deleteButton: ImageButton = itemView.findViewById(R.id.btnDelete)
        val enabledToggle: Switch = itemView.findViewById(R.id.switchEnabled)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int ): ReminderViewHolder {
        alarmUtils = AlarmUtils(parent.context)
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_reminder, parent, false
        )
        return ReminderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val currentReminder = reminders[position]

        holder.titleTextView.text = currentReminder.title
        holder.timeTextView.text = alarmUtils.getFormattedTime(currentReminder.timeInMillis)
        holder.dateTextView.text = alarmUtils.getFormattedDate(currentReminder.timeInMillis)

        val repeatText = when {
            !currentReminder.isRepeating -> "One time"
            else -> when(currentReminder.repeatInterval) {
                Reminder.RepeatInterval.DAILY -> "Daily"
                Reminder.RepeatInterval.WEEKLY -> "Weekly"
                Reminder.RepeatInterval.MONTHLY -> "Monthly"
                else -> "One-time"
            }
        }

        holder.repeatTextView.text = repeatText
        // Switch handling with listener management
        holder.enabledToggle.apply {
            // Remove previous listener to prevent recycling issues
            setOnCheckedChangeListener(null)

            // Set current state
            isChecked = currentReminder.isEnabled

            // Set new listener
            setOnCheckedChangeListener { _, isChecked ->
                onToggleEnabled(currentReminder, isChecked)
            }
        }

        // Click listeners
        holder.itemView.setOnClickListener {
            onReminderClickListener(currentReminder)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClickListener(currentReminder)
        }
    }

    override fun getItemCount(): Int {
        return reminders.size
    }

    fun updateReminders(newReminders: List<Reminder>) {
        this.reminders = newReminders
        notifyDataSetChanged()
    }
}