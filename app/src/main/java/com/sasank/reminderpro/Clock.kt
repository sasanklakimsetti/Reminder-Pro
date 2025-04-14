package com.sasank.reminderpro

import android.content.Context
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class Clock : Fragment() {

    private lateinit var mainClock: TextView
    private lateinit var currentCountry: TextView
    private lateinit var timeZoneRecyclerView: RecyclerView
    private lateinit var addTimeZoneFab: FloatingActionButton

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var updateRunnable: Runnable

    private val additionalTimezones = mutableListOf<String>()
    private lateinit var adapter: TimeZoneAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_clock, container, false)

        mainClock = view.findViewById(R.id.mainClock)
        currentCountry = view.findViewById(R.id.currentCountry)
        timeZoneRecyclerView = view.findViewById(R.id.timeZoneRecyclerView)
        addTimeZoneFab = view.findViewById(R.id.addTimeZoneFab)

        adapter = TimeZoneAdapter(requireContext(), additionalTimezones) { zoneId ->
            additionalTimezones.remove(zoneId)
            adapter.notifyDataSetChanged()
            saveTimezones(requireContext(), additionalTimezones)
        }

        timeZoneRecyclerView.adapter = adapter
        timeZoneRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        ItemTouchHelper(TimeZoneTouchHelperCallback(adapter)).attachToRecyclerView(timeZoneRecyclerView)

        loadTimezones(requireContext()).let {
            additionalTimezones.addAll(it)
        }

        addTimeZoneFab.setOnClickListener {
            showTimezonePicker()
        }

        startClock()

        return view
    }

    private fun showTimezonePicker() {
        val timezones = TimeZone.getAvailableIDs().sorted()
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Choose a Timezone")
        builder.setItems(timezones.toTypedArray()) { _, which ->
            val selectedZone = timezones[which]
            if (!additionalTimezones.contains(selectedZone)) {
                additionalTimezones.add(selectedZone)
                saveTimezones(requireContext(), additionalTimezones)
                adapter.notifyItemInserted(additionalTimezones.size - 1)
            } else {
                Toast.makeText(requireContext(), "Already added", Toast.LENGTH_SHORT).show()
            }
        }
        builder.show()
    }

    private fun startClock() {
        updateRunnable = object : Runnable {
            override fun run() {
                val now = Calendar.getInstance()
                val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val localZone = TimeZone.getDefault()
                sdf.timeZone = localZone
                mainClock.text = sdf.format(now.time)
                currentCountry.text = localZone.id.substringAfter("/").replace("_", " ")
                adapter.notifyDataSetChanged()
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(updateRunnable)
    }

    private fun saveTimezones(context: Context, list: List<String>) {
        val prefs = context.getSharedPreferences("ClockPrefs", Context.MODE_PRIVATE)
        prefs.edit().putStringSet("timezones", list.toSet()).apply()
    }

    private fun loadTimezones(context: Context): List<String> {
        val prefs = context.getSharedPreferences("ClockPrefs", Context.MODE_PRIVATE)
        return prefs.getStringSet("timezones", emptySet())?.toList() ?: emptyList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(updateRunnable)
    }
}