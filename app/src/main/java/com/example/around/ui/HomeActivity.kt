package com.example.around.ui

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import com.example.around.R
import com.example.around.di.AppGraph
import com.example.around.ui.base.BaseActivity
import com.example.around.util.CityNormalizer
import com.example.around.util.NavigationKeys
import java.util.Calendar

class HomeActivity : BaseActivity() {

    private var detectedCity: String = "Tel Aviv"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupBottomNav(R.id.nav_home)

        val greetingTv = findViewById<TextView>(R.id.tvGreeting)
        val timeSpinner = findViewById<Spinner>(R.id.spinnerTimeOverride)
        val citySpinner = findViewById<Spinner>(R.id.spinnerCityOverride)

        detectedCity = CityNormalizer.canonical(
            intent.getStringExtra(NavigationKeys.EXTRA_CITY) ?: "Tel Aviv"
        )

        val autoTimeContext = getAutomaticTimeContext()

        setupGreeting(greetingTv, autoTimeContext)
        setupGridAnimation()
        setSpinnerToAutoTime(timeSpinner, autoTimeContext)
        setupCitySpinner(citySpinner)
        setupMoodButtons(timeSpinner, citySpinner)
    }

    override fun onResume() {
        super.onResume()
        refreshBottomNavSelection(R.id.nav_home)
    }

    override fun onStart() {
        super.onStart()

        if (!isUserLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun setupGreeting(greetingTv: TextView, autoTimeContext: String) {
        val emoji = when (autoTimeContext) {
            "Morning" -> "☀️"
            "Afternoon" -> "🌤️"
            else -> "🌙"
        }

        AppGraph.authUseCase.getCurrentUserFirstName { firstName ->
            runOnUiThread {
                val safeName = if (firstName.isNullOrBlank()) "there" else firstName

                greetingTv.text = when (autoTimeContext) {
                    "Morning" -> "Good morning, $safeName $emoji"
                    "Afternoon" -> "Good afternoon, $safeName $emoji"
                    else -> "Good evening, $safeName $emoji"
                }
            }
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

    private fun setupCitySpinner(citySpinner: Spinner) {
        val baseCities = resources.getStringArray(R.array.cities_options)
            .map { CityNormalizer.canonical(it) }
            .distinct()
            .filter { it.isNotBlank() }
            .filterNot { it.equals(detectedCity, ignoreCase = true) }

        val cityOptions = mutableListOf<String>()
        cityOptions.add("Near me ($detectedCity)")
        cityOptions.addAll(baseCities)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            cityOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        citySpinner.adapter = adapter
        citySpinner.setSelection(0)
    }

    private fun setupMoodButtons(timeSpinner: Spinner, citySpinner: Spinner) {
        fun selectedTime(): String {
            return timeSpinner.selectedItem?.toString() ?: getAutomaticTimeContext()
        }

        fun selectedCity(): String {
            val selected = citySpinner.selectedItem?.toString()?.trim().orEmpty()

            return if (selected.startsWith("Near me", ignoreCase = true) || selected.isBlank()) {
                detectedCity
            } else {
                CityNormalizer.canonical(selected)
            }
        }

        findViewById<LinearLayout>(R.id.moodFoody).setOnClickListener {
            openTourList("culinary", selectedTime(), selectedCity())
        }

        findViewById<LinearLayout>(R.id.moodCulture).setOnClickListener {
            openTourList("culture", selectedTime(), selectedCity())
        }

        findViewById<LinearLayout>(R.id.moodRelax).setOnClickListener {
            openTourList("relax", selectedTime(), selectedCity())
        }

        findViewById<LinearLayout>(R.id.moodSurprise).setOnClickListener {
            openTourList("surprise", selectedTime(), selectedCity())
        }
    }

    private fun openTourList(mood: String, time: String, city: String) {
        val intent = Intent(this, TourListActivity::class.java)
        intent.putExtra(NavigationKeys.EXTRA_MOOD, mood)
        intent.putExtra(NavigationKeys.EXTRA_TIME, time)
        intent.putExtra(NavigationKeys.EXTRA_CITY, CityNormalizer.canonical(city))
        startActivity(intent)
    }
}