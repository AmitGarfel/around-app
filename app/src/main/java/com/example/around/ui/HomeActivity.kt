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
import com.example.around.data.preferences.UserPrefsProvider
import com.example.around.di.AppGraph
import com.example.around.ui.base.BaseActivity
import com.example.around.ui.formatters.HomeGreetingFormatter
import com.example.around.ui.providers.HomeArgsProvider
import com.example.around.ui.providers.HomeSelectionProvider
import com.example.around.ui.providers.TimeContextProvider
import com.example.around.util.CityNormalizer
import com.example.around.util.NavigationKeys

class HomeActivity : BaseActivity() {

    private lateinit var userPrefs: UserPrefsProvider

    private var detectedCity: String = "Tel Aviv"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupBottomNav(R.id.nav_home)

        val greetingTv = findViewById<TextView>(R.id.tvGreeting)
        val timeSpinner = findViewById<Spinner>(R.id.spinnerTimeOverride)
        val citySpinner = findViewById<Spinner>(R.id.spinnerCityOverride)

        userPrefs = UserPrefsProvider(this)
        detectedCity = HomeArgsProvider.resolveCity(intent, userPrefs)

        val autoTimeContext = TimeContextProvider.getAutomaticTimeContext()

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
        AppGraph.authUseCase.getCurrentUserFirstName { firstName ->
            runOnUiThread {
                greetingTv.text = HomeGreetingFormatter.buildGreeting(firstName, autoTimeContext)
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
        bindMoodClick(R.id.moodFoody, "culinary", timeSpinner, citySpinner)
        bindMoodClick(R.id.moodCulture, "culture", timeSpinner, citySpinner)
        bindMoodClick(R.id.moodRelax, "relax", timeSpinner, citySpinner)
        bindMoodClick(R.id.moodSurprise, "surprise", timeSpinner, citySpinner)
    }

    private fun bindMoodClick(
        viewId: Int,
        mood: String,
        timeSpinner: Spinner,
        citySpinner: Spinner
    ) {
        findViewById<LinearLayout>(viewId).setOnClickListener {
            openTourList(
                mood,
                HomeSelectionProvider.resolveSelectedTime(
                    timeSpinner,
                    TimeContextProvider.getAutomaticTimeContext()
                ),
                HomeSelectionProvider.resolveSelectedCity(citySpinner, detectedCity)
            )
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