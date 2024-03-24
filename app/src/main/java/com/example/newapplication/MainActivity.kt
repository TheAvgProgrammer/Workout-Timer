package com.example.newapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var start: Button = findViewById(R.id.startbtn)
        var timeField: EditText = findViewById(R.id.editTextTime)

        start.setOnClickListener{
            var time: String = timeField.text.toString()

            if(time == ""){
                Toast.makeText(this, "Time Cannot Be Empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            var timerIntent = Intent(this, SecondActivity::class.java)
            timerIntent.putExtra("Time", time)
            startActivity(timerIntent)
        }
    }
}