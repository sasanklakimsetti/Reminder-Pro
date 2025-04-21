package com.sasank.reminderpro

data class AlarmData(
    val id: Int,
    var hour: Int,
    var minute: Int,
    var time: String,
    var title: String,
    var repeatDays: List<String>,
    var isEnabled: Boolean
)
