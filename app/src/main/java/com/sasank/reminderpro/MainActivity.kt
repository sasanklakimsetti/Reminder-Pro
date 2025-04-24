package com.sasank.reminderpro

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sasank.reminderpro.ui.alarm.AlarmFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val bottomNavigationView=findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            .setOnItemSelectedListener {
                menuItem->
                when(menuItem.itemId){
                    R.id.clock->{
                        replaceFragment(Clock())
                        true
                    }
                    R.id.alarm->{
                        replaceFragment(AlarmFragment())
                        true
                    }
                    R.id.reminder->{
                        replaceFragment(ReminderFragment())
                        true
                    }
                    else->{
                        false
                    }
                }
            }
        replaceFragment(Clock())
    }

    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout,fragment).commit()
    }
}