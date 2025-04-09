package com.sasank.reminderpro

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Calendar
import java.util.Collections
import java.util.Locale
import java.util.TimeZone

class Clock : Fragment(){
    private lateinit var mainClock: TextView
    private lateinit var timeZoneContainer: LinearLayout
    private lateinit var addTimezoneFab: FloatingActionButton
    private lateinit var currentCountry: TextView

    private val handler= Handler(Looper.getMainLooper())
    private lateinit var updateRunnable: Runnable

    private val additionalTimezones: MutableList<String> by lazy {
        loadTimezones(requireContext())
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_clock,container,false)
        mainClock=view.findViewById(R.id.mainClock)
        timeZoneContainer=view.findViewById(R.id.timeZoneContainer)
        addTimezoneFab=view.findViewById(R.id.addTimeZoneFab)
        currentCountry=view.findViewById(R.id.currentCountry)

        startClock()

        addTimezoneFab.setOnClickListener {
            showTimezonePicker()
        }
        additionalTimezones.forEach { addTimezoneCard(it) }
        return view
    }

    private fun startClock(){
        updateRunnable =object : Runnable{
            override fun run(){
                val now= Calendar.getInstance()
                val localTimeZone= TimeZone.getDefault()
                val localFormat= SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                localFormat.timeZone=localTimeZone

                mainClock.text=localFormat.format(now.time)
                currentCountry.text=getCountryFromZone(localTimeZone.id)

                for(i in 0 until timeZoneContainer.childCount){
                    val card=timeZoneContainer.getChildAt(i) as CardView
                    val textView=card.findViewById<TextView>(R.id.timezoneTimeText)
                    val zoneId=additionalTimezones[i]
                    val sdf= SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    sdf.timeZone= TimeZone.getTimeZone(zoneId)
                    textView.text="${sdf.format(now.time)} ($zoneId)"
                }
                handler.postDelayed(this,100)
            }
        }
        handler.post(updateRunnable)
    }

    private fun getCountryFromZone(zoneId: String): String{
        return zoneId.substringAfter("/").replace("_"," ")
    }

    private fun showTimezonePicker(){
        val timezones= TimeZone.getAvailableIDs().sorted()
        val builder= AlertDialog.Builder(requireContext())
        builder.setTitle("Choose a Timezones")

        builder.setItems(timezones.toTypedArray()) { _, which ->
            val selectedZone=timezones[which]
            if(!additionalTimezones.contains(selectedZone)){
                additionalTimezones.add(selectedZone)
                saveTimezones(requireContext(), additionalTimezones)
                addTimezoneCard(selectedZone)
            }
            else Toast.makeText(requireContext(),"Already added", Toast.LENGTH_SHORT).show()
        }
        builder.show()
    }

    private fun addTimezoneCard(zoneId: String){
        val card=layoutInflater.inflate(R.layout.card_timezone, timeZoneContainer, false)
        val textView=card.findViewById<TextView>(R.id.timezoneTimeText)
        textView.text=zoneId

        // long press to delete the card which was added before
        card.setOnLongClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Timezone")
                .setMessage("Remove $zoneId?")
                .setPositiveButton("Delete") { _, _ ->
                    timeZoneContainer.removeView(card)
                    additionalTimezones.remove(zoneId)
                    saveTimezones(requireContext(), additionalTimezones)
                }
                .setNegativeButton("Cancel", null)
                .show()
            true
        }
        timeZoneContainer.addView(card)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(updateRunnable)
    }

    // sharedpreferences utility
    private fun saveTimezones(context: Context, timezones: List<String>){
        val prefs=context.getSharedPreferences("ClockPrefs", Context.MODE_PRIVATE)
        prefs.edit().putStringSet("timezone_list", timezones.toSet()).apply()
    }

    private fun loadTimezones(context: Context): MutableList<String>{
        val prefs=context.getSharedPreferences("ClockPrefs",Context.MODE_PRIVATE)
        return prefs.getStringSet("timezone_list", emptySet())?.toMutableList()?:mutableListOf()
    }
}


//package com.sasank.reminderpro

//import android.annotation.SuppressLint
//import android.content.Context
//import android.os.Bundle
//import android.os.Handler
//import android.os.Looper
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.google.android.material.floatingactionbutton.FloatingActionButton
//import java.text.SimpleDateFormat
//import java.util.*
//
//class Clock : Fragment() {
//
//    private lateinit var mainClock: TextView
//    private lateinit var currentCountry: TextView
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var deleteButton: View
//    private lateinit var addFab: FloatingActionButton
//
//    private val handler = Handler(Looper.getMainLooper())
//    private lateinit var updateRunnable: Runnable
//
//    private val selectedItems = mutableSetOf<String>()
//    private val timezones = mutableListOf<String>()
//    private lateinit var adapter: TimeZoneAdapter
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_clock, container, false)
//
//        mainClock = view.findViewById(R.id.mainClock)
//        currentCountry = view.findViewById(R.id.currentCountry)
//        recyclerView = view.findViewById(R.id.timeZoneRecycler)
//        deleteButton = view.findViewById(R.id.deleteButton)
//        addFab = view.findViewById(R.id.addTimeZoneFab)
//
//        timezones.addAll(loadTimezones(requireContext()))
//        adapter = TimeZoneAdapter(timezones, selectedItems)
//
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        recyclerView.adapter = adapter
//
//        deleteButton.setOnClickListener {
//            timezones.removeAll(selectedItems)
//            saveTimezones(requireContext(), timezones)
//            adapter.removeSelected()
//            deleteButton.visibility = View.GONE
//        }
//
//        addFab.setOnClickListener { showTimezonePicker() }
//
//        startClock()
//        return view
//    }
//
//    private fun startClock() {
//        updateRunnable = object : Runnable {
//            override fun run() {
//                val now = Calendar.getInstance()
//                val localTZ = TimeZone.getDefault()
//                val format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
//                format.timeZone = localTZ
//
//                mainClock.text = format.format(now.time)
//                currentCountry.text = getCountryFromZone(localTZ.id)
//
//                handler.postDelayed(this, 1000)
//            }
//        }
//        handler.post(updateRunnable)
//    }
//
//    private fun showTimezonePicker() {
//        val zones = TimeZone.getAvailableIDs().sorted()
//        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
//        builder.setTitle("Select Timezone")
//        builder.setItems(zones.toTypedArray()) { _, which ->
//            val selected = zones[which]
//            if (!timezones.contains(selected)) {
//                timezones.add(selected)
//                saveTimezones(requireContext(), timezones)
//                adapter.notifyItemInserted(timezones.size - 1)
//            }
//        }
//        builder.show()
//    }
//
//    private fun getCountryFromZone(zoneId: String): String {
//        return zoneId.substringAfter("/").replace("_", " ")
//    }
//
//    private fun saveTimezones(context: Context, timezones: List<String>) {
//        val prefs = context.getSharedPreferences("ClockPrefs", Context.MODE_PRIVATE)
//        prefs.edit().putStringSet("timezone_list", timezones.toSet()).apply()
//    }
//
//    private fun loadTimezones(context: Context): MutableList<String> {
//        val prefs = context.getSharedPreferences("ClockPrefs", Context.MODE_PRIVATE)
//        return prefs.getStringSet("timezone_list", emptySet())?.toMutableList() ?: mutableListOf()
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        handler.removeCallbacks(updateRunnable)
//    }
//}
