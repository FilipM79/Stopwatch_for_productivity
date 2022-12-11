package org.hyperskill.stopwatch

import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat.getSystemService
import org.hyperskill.stopwatch.databinding.ActivityMainBinding

const val CHANNEL_ID = "org.hyperskill"
const val NOTIFICATION_ID = 393939

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val colors = arrayOf(Color.RED, Color.GREEN, Color.BLUE)
    private var color = colors[0]
    lateinit var notification: Notification
    lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Notification")
            .setContentText("Time exceeded")
            .setStyle(NotificationCompat.BigTextStyle())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .build()
        notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.startButton.setOnClickListener(::startTimer)
        binding.resetButton.setOnClickListener(::resetTimer)
        binding.settingsButton.setOnClickListener(::settingsClick)
        binding.progressBar.visibility = View.INVISIBLE
    }

    private var secondsPassed = 0
    private val timeString: String
        get() = "%02d:%02d".format(secondsPassed / 60, secondsPassed % 60)
    private var timerIsOn = false
    private var timeLimit: Int = Int.MAX_VALUE
    private val handler = Handler(Looper.getMainLooper())

    private val timerTick: Runnable = object : Runnable {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun run() {
            binding.textView.text = timeString
            color = colors[(colors.indexOf(color) + 1) % colors.size]
            binding.progressBar.indeterminateTintList = ColorStateList.valueOf(color)
            if (secondsPassed > timeLimit) {
                binding.textView.setTextColor(Color.RED)
                if (timeLimit > 0) {
                    notificationManager.notify(NOTIFICATION_ID, notification)
                    notification.flags = Notification.FLAG_INSISTENT or Notification.FLAG_ONLY_ALERT_ONCE
                }
            }
            secondsPassed++
            handler.postDelayed(this, 1000)
        }
    }

    private fun startTimer(view: View) {
        if (!timerIsOn) {
            timerIsOn = true
            handler.post(timerTick)
            binding.progressBar.visibility = View.VISIBLE
            binding.settingsButton.isEnabled = false
        }
    }

    private fun resetTimer(view: View) {
        timerIsOn = false
        binding.progressBar.visibility = View.INVISIBLE
        binding.settingsButton.isEnabled = true
        handler.removeCallbacks(timerTick)
        secondsPassed = 0
        binding.textView.text = timeString
        binding.textView.setTextColor(Color.BLACK)
    }

    private fun settingsClick(view: View) {
        val contentView = LayoutInflater.from(this).inflate(R.layout.settings_dialog, null, false)
        AlertDialog.Builder(this)
            .setTitle(R.string.settings_title)
            .setView(contentView)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val editText = contentView.findViewById<EditText>(R.id.upperLimitEditText)
                timeLimit = editText.text.toString().toIntOrNull() ?: Int.MAX_VALUE
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(timerTick)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "My Notification"
            val descriptionText = "This is the text of my notification"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                lightColor = Color.RED
                enableLights(true)
            }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}