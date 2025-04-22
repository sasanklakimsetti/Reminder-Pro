package com.sasank.reminderpro.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sasank.reminderpro.entities.AlarmEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmEntity)

    @Query("SELECT * FROM alarms ORDER BY timeInMillis ASC")
    fun getAllAlarms(): Flow<List<AlarmEntity>>

    @Query("UPDATE alarms SET isEnabled= :enabled where id= :id")
    suspend fun updateAlarmState(id: Int, enabled: Boolean)

    @Delete
    suspend fun deleteAlarm(alarm: AlarmEntity)

}