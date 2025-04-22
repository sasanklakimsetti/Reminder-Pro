package com.sasank.reminderpro.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey val id: Int,
    val label: String,
    val hour: Int,
    val minute: Int,
    val isEnabled: Boolean,
    val timeInMillis: Long
)
