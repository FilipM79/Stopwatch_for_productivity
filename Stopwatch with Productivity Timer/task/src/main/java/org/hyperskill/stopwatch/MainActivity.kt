package org.hyperskill.stopwatch

import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), Runnable {

    private lateinit var startButton: Button
    private lateinit var resetButton: Button
    private lateinit var timerView: TextView
    private val handler = Handler()
    private var count = 0
    private var isCounting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton = findViewById(R.id.startButton)
        resetButton = findViewById(R.id.resetButton)
        timerView = findViewById(R.id.textView)

        formatTimer(count)

        startButton.setOnClickListener {
            if (!isCounting) {
                formatTimer(count)
                isCounting = true
                handler.postDelayed(this, 1000)
            } else {
//                count = 1
                formatTimer(count)
            }
        }

        resetButton.setOnClickListener {
            isCounting = false
            count = 0
            formatTimer(count)
        }
    }

    override fun run() {
        if (isCounting) {
            count++
            formatTimer(count)

//            if (count == 0) {
//                isCounting = false
//                count = 1
//                return
//            }

            handler.postDelayed(this, 1000)
        }
    }

    fun formatTimer(count: Int) {
        val remaining = count.toString().padStart(4, '0')
        val formatted = "${remaining.substring(0, 2)}:${remaining.substring(2)}"
        timerView.text = formatted
    }
}