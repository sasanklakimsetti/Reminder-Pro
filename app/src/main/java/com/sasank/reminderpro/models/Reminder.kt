package com.sasank.reminderpro.models
import java.io.Serializable

data class Reminder(
    val id: Int,
    var title: String,
    var description: String = "",
    var timeInMillis: Long,
    var isRepeating: Boolean = false,
    var repeatInterval: RepeatInterval = RepeatInterval.NONE,
    var isEnabled: Boolean = true
) : Serializable {
    enum class RepeatInterval {
        NONE, DAILY, WEEKLY, MONTHLY
    }
}