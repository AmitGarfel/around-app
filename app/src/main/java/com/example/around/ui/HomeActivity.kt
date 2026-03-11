package com.example.around.ui

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import com.example.around.R
import com.example.around.ui.base.BaseActivity
import java.util.Calendar

class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupBottomNav(R.id.nav_menu)

        val greetingTv = findViewById<TextView>(R.id.tvGreeting)
        val timeSpinner = findViewById<Spinner>(R.id.spinnerTimeOverride)

        val userName = "Amit" // later: from Firestore

        val autoTimeContext = getAutomaticTimeContext()

        val emoji = when (autoTimeContext) {
            "Morning" -> "☀️"
            "Afternoon" -> "🌤️"
            else -> "🌙"
        }

        greetingTv.text = when (autoTimeContext) {
            "Morning" -> "Good morning, $userName $emoji"
            "Afternoon" -> "Good afternoon, $userName $emoji"
            else -> "Good evening, $userName $emoji"
        }

        setupGridAnimation()
        setSpinnerToAutoTime(timeSpinner, autoTimeContext)
        setupMoodButtons(timeSpinner)
    }

    override fun onStart() {
        super.onStart()

        if (!isUserLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun setupGridAnimation() {
        val gridLayout = findViewById<GridLayout>(R.id.moodGrid)
        for (i in 0 until gridLayout.childCount) {
            val child = gridLayout.getChildAt(i)
            val anim = AnimationUtils.loadAnimation(this, R.anim.item_fall_down).apply {
                startOffset = (i * 80).toLong()
            }
            child.startAnimation(anim)
        }
    }

    private fun getAutomaticTimeContext(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> "Morning"
            in 12..17 -> "Afternoon"
            else -> "Evening"
        }
    }

    private fun setSpinnerToAutoTime(spinner: Spinner, autoTime: String) {
        val options = resources.getStringArray(R.array.time_options)
        val index = options.indexOf(autoTime)
        if (index >= 0) spinner.setSelection(index)
    }

    private fun setupMoodButtons(timeSpinner: Spinner) {
        fun selectedTime(): String =
            timeSpinner.selectedItem?.toString() ?: getAutomaticTimeContext()

        findViewById<LinearLayout>(R.id.moodFoody).setOnClickListener {
            openTourList("culinary", selectedTime())
        }
        findViewById<LinearLayout>(R.id.moodCulture).setOnClickListener {
            openTourList("culture", selectedTime())
        }
        findViewById<LinearLayout>(R.id.moodRelax).setOnClickListener {
            openTourList("relax", selectedTime())
        }
        findViewById<LinearLayout>(R.id.moodSurprise).setOnClickListener {
            openTourList("surprise", selectedTime())
        }
    }

    private fun openTourList(mood: String, time: String) {
        val intent = Intent(this, TourListActivity::class.java)
        intent.putExtra("MOOD", mood)
        intent.putExtra("TIME", time)
        startActivity(intent)
    }
}
