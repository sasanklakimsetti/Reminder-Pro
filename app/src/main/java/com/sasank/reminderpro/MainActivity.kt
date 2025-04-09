package com.sasank.reminderpro

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

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
                        replaceFragment(Alarm())
                        true
                    }
                    R.id.reminder->{
                        replaceFragment(Reminder())
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