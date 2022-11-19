package org.hyperskill.tests.stopwatch

import android.widget.Button
import android.widget.TextView
import org.hyperskill.stopwatch.MainActivity
import org.hyperskill.tests.stopwatch.internals.StopwatchUnitTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// Version 2.0
@RunWith(RobolectricTestRunner::class)
class Stage1UnitTest : StopwatchUnitTest<MainActivity>(MainActivity::class.java) {


    private val startButton: Button by lazy {
        val view = activity.findViewByString<Button>("startButton")

        val message = "For view with id \"startButton\", in property \"text\""
        assertEquals(message, "start", view.text.toString().lowercase())

        view
    }

    private val resetButton: Button by lazy {
        val view = activity.findViewByString<Button>("resetButton")

        val message = "For view with id \"resetButton\", in property \"text\""
        assertEquals(message, "reset", view.text.toString().lowercase())

        view
    }

    private val textView: TextView by lazy {
        activity.findViewByString("textView")
    }


    @Test
    fun checkStartButton() {
        testActivity {
            startButton
        }
    }

    @Test
    fun checkResetButton() {
        testActivity {
            resetButton
        }
    }

    @Test
    fun checkTextView() {
        testActivity {
            textView
        }
    }
}