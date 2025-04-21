package com.sasank.reminderpro

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object AlarmStorage {
    private const val PREF_NAME="alarm_pref"
    private const val KEY_ALARMS="alarms"

    fun saveAlarms(context: Context, alarmList: List<AlarmData>){
        val prefs=context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor=prefs.edit()
        val json= Gson().toJson(alarmList)
        editor.putString(KEY_ALARMS, json)
        editor.apply()
    }

    fun loadAlarms(context: Context): MutableList<AlarmData>{
        val prefs=context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json=prefs.getString(KEY_ALARMS,null)
        return if(json!=null){
            val type=object : TypeToken<MutableList<AlarmData>>(){}.type
            Gson().fromJson(json, type)
        } else mutableListOf()
    }
}