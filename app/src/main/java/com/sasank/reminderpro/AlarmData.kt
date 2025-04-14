package com.sasank.reminderpro

data class AlarmData(
    var time: String,
    var title: String,
    var days: List<String>,
    var isEnabled: Boolean
)
