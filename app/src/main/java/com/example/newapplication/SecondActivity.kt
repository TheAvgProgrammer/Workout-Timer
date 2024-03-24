package com.example.newapplication

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class SecondActivity : ComponentActivity() {
    @SuppressLint("SetTextI18n")

    var timeCountDown: CountDownTimer? = null
    var timeRemaining: Long = 0
    var isPaused: Boolean = false

    lateinit var pauseButton: Button
    lateinit var resetButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        //Checking if the notification's permissions are given
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }



        var timeString: String = getRecievedTime()
        val millisec  = getMinutesAndSeconds(timeString)

        pauseButton = findViewById(R.id.pause)
        resetButton = findViewById(R.id.reset)

        pauseButton.setOnClickListener{
            if(isPaused){
                resume()
                pauseButton.text = "Pause"
            }
            else{
                pause()
                pauseButton.text = "Resume"
            }
        }

        resetButton.setOnClickListener {
            timeCountDown?.cancel()
            timeRemaining = 0
            isPaused = false
            pauseButton.text = "Pause"
            timeString = ""

            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        }

        startTimer(millisec)
    }


    private fun getMinutesAndSeconds(timeString: String): Long {
        val arr = timeString.split(":")
        val min = arr[0].toLong()
        val secs = arr[1].toLong()
        return min*60000 + secs*1000
    }

    private fun setMinutesAndSeconds(timeLong: Long): String {
        var min:String
        var secs:String
        var tString: String
        if(timeLong >= 60000){
            min = (timeLong/60000).toInt().toString()
            if(min.toInt()<10){
                min = "0$min"
            }
            println(timeLong%60000)
            secs = (timeLong%60000/1000).toInt().toString()
            if(secs.toInt()<10){
                secs = "0$secs"
            }

            tString = "$min:$secs"
        }
        else{
            secs = (timeLong/1000).toInt().toString()
            if(secs.toInt()<10){
                secs = "0$secs"
            }
            tString = "00:$secs"
        }
        return tString
    }

    private fun startTimer(time: Long) {
        timeCountDown = object :CountDownTimer(time, 1000){
            override fun onTick(millisUntilFinished: Long) {
                val strTime = setMinutesAndSeconds(millisUntilFinished)
                val timeView: TextView = findViewById(R.id.timer)
                timeView.text = strTime

                timeRemaining = millisUntilFinished
            }

            override fun onFinish() {
                makeNotification()
            }
        }
        timeCountDown?.start()
    }

    private fun getRecievedTime(): String {
        val bundle: Bundle? = intent.extras
        return bundle?.getString("Time").toString()
    }

    private fun pause(){
        timeCountDown?.cancel()
        isPaused = true
    }

    private fun resume(){
        startTimer(timeRemaining)
        isPaused = false
    }

    private fun makeNotification(){
        val channelID = "CHANNEL_ID_NOTIFICATION"
        val textTitle = "Time's Upp!!!"
        val textContent = "You're time is up!!. Go do your next set lazy ass"

        //Making a notification builder
        val builder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.drawable.baseline_access_alarm_24)
            .setContentTitle(textTitle)
            .setContentText(textContent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notifIntent = Intent(this, MainActivity::class.java)
        notifIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingnotifIntent: PendingIntent = PendingIntent
            .getActivity(this, 0, notifIntent, PendingIntent.FLAG_MUTABLE)

        builder.setContentIntent(pendingnotifIntent)
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //Making a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, builder.build())
    }

}