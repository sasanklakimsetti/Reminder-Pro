package com.sasank.reminderpro

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val titleTextView: TextView = findViewById(R.id.textView1)
        val title = "ReminderPro"

        val displayedText = StringBuilder()

        // Create a handler to introduce delays between each character update
        val handler = Handler(mainLooper)

        // Use a loop with a delay to show each character one by one
        for (i in 0 until title.length) {
            handler.postDelayed({
                displayedText.append(title[i])
                titleTextView.text = displayedText.toString()
            }, i * 100L) // 200ms delay between each character
        }

        // After 5 seconds, start the MainActivity
        Handler(mainLooper).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, title.length * 100L + 300L)
    }
}
