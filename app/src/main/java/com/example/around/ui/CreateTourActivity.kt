package com.example.around.ui

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.around.R
import com.example.around.di.AppGraph
import com.example.around.domain.model.Station
import com.example.around.domain.model.Tour
import com.example.around.ui.base.BaseActivity
import com.example.around.ui.helpers.CreateTourDurationHelper
import com.example.around.ui.helpers.CreateTourHelper
import com.example.around.util.CityNormalizer
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.coroutines.launch

class CreateTourActivity : BaseActivity() {

    private val createTourUseCase = AppGraph.createTourUseCase
    private var selectedImageUri: Uri? = null

    private data class SelectedStationData(
        val name: String,
        val query: String,
        val latLng: LatLng
    )

    private val selectedStations = mutableMapOf<Int, SelectedStationData>()
    private var activeStationIndex: Int = -1

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                findViewById<ImageView?>(R.id.ivPreview)?.let { preview ->
                    preview.visibility = ImageView.VISIBLE
                    preview.setImageURI(uri)
                }
                Toast.makeText(this, "Image selected successfully ✅", Toast.LENGTH_SHORT).show()
            }
        }

    private val placePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            when (result.resultCode) {

                RESULT_OK -> {
                    if (result.data != null && activeStationIndex != -1) {
                        val place = Autocomplete.getPlaceFromIntent(result.data!!)
                        val latLng = place.latLng
                        val name = place.name?.trim().orEmpty()
                        val address = place.address?.trim().orEmpty()

                        if (latLng != null && name.isNotBlank()) {
                            selectedStations[activeStationIndex] = SelectedStationData(
                                name = name,
                                query = address.ifBlank { name },
                                latLng = latLng
                            )
                            updateStationField(activeStationIndex, name)
                        } else {
                            Toast.makeText(
                                this,
                                "Failed to get station location",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                AutocompleteActivity.RESULT_ERROR -> {
                    val data = result.data
                    if (data != null) {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Toast.makeText(
                            this,
                            "Places error: ${status.statusMessage ?: status.statusCode}",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(this, "Places error: unknown error", Toast.LENGTH_LONG).show()
                    }
                }

                RESULT_CANCELED -> {
                    // user canceled
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_tour)

        setupBottomNav(R.id.nav_settings)
        setupBackButton()
        initPlacesIfNeeded()
        setupSpinners()
        setupCityAutocomplete()
        setupStationPickers()
        updateDurationDefault()

        findViewById<Button>(R.id.btnPickImage).setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        findViewById<Button>(R.id.btnSaveRoute).setOnClickListener {
            saveTour()
        }

        findViewById<Button?>(R.id.btnExit)?.setOnClickListener {
            finish()
        }
    }

    private fun setupBackButton() {
        findViewById<ImageButton?>(R.id.btnBack)?.setOnClickListener {
            finish()
        }
    }

    private fun initPlacesIfNeeded() {
        if (Places.isInitialized()) return

        val apiKey = getMapsApiKeyFromManifest()
        if (apiKey.isBlank()) {
            Toast.makeText(this, "Google Maps API key is missing", Toast.LENGTH_LONG).show()
            return
        }

        Places.initialize(applicationContext, apiKey)
    }

    private fun getMapsApiKeyFromManifest(): String {
        return try {
            val appInfo = packageManager.getApplicationInfo(
                packageName,
                PackageManager.GET_META_DATA
            )
            appInfo.metaData?.getString("com.google.android.geo.API_KEY").orEmpty()
        } catch (e: Exception) {
            ""
        }
    }

    private fun setupSpinners() {
        fun setAdapter(spinner: Spinner, arrayRes: Int) {
            val adapter = ArrayAdapter.createFromResource(
                this,
                arrayRes,
                R.layout.spinner_item_white
            )
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_white)
            spinner.adapter = adapter
        }

        setAdapter(findViewById(R.id.spinnerMood), R.array.mood_options)
        setAdapter(findViewById(R.id.spinnerTimeFit), R.array.time_options)
        setAdapter(findViewById(R.id.spinnerDuration), R.array.duration_options)
    }

    private fun setupCityAutocomplete() {
        val cityAuto = findViewById<AutoCompleteTextView>(R.id.etCity)

        val cities = resources.getStringArray(R.array.cities_options)
            .map { CityNormalizer.canonical(it) }
            .distinct()

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            cities
        )

        cityAuto.setAdapter(adapter)
        cityAuto.threshold = 1
        cityAuto.setOnClickListener { cityAuto.showDropDown() }
    }

    private fun setupStationPickers() {
        val ids = listOf(
            R.id.etStation1,
            R.id.etStation2,
            R.id.etStation3,
            R.id.etStation4
        )

        ids.forEachIndexed { index, id ->
            val et = findViewById<EditText>(id)

            et.keyListener = null
            et.isFocusable = false
            et.isFocusableInTouchMode = false
            et.isCursorVisible = false
            et.isClickable = true
            et.isLongClickable = false

            et.setOnClickListener {
                activeStationIndex = index

                val fields = listOf(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.ADDRESS,
                    Place.Field.LAT_LNG
                )

                val city = findViewById<AutoCompleteTextView>(R.id.etCity)
                    .text.toString()
                    .trim()

                val currentText = et.text.toString().trim()

                val initialQuery = when {
                    currentText.isNotBlank() && city.isNotBlank() -> "$currentText $city"
                    currentText.isNotBlank() -> currentText
                    city.isNotBlank() -> city
                    else -> ""
                }

                val builder = Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN,
                    fields
                )
                    .setCountries(listOf("IL"))
                    .setHint("Search place name + city")

                if (initialQuery.isNotBlank()) {
                    builder.setInitialQuery(initialQuery)
                }

                val intent = builder.build(this)
                placePickerLauncher.launch(intent)
            }
        }
    }
    private fun updateStationField(index: Int, value: String) {
        val id = when (index) {
            0 -> R.id.etStation1
            1 -> R.id.etStation2
            2 -> R.id.etStation3
            3 -> R.id.etStation4
            else -> return
        }

        findViewById<EditText>(id).setText(value)
        updateDurationDefault()
    }

    private fun updateDurationDefault() {
        val stationTexts = listOf(
            findViewById<EditText>(R.id.etStation1).text.toString(),
            findViewById<EditText>(R.id.etStation2).text.toString(),
            findViewById<EditText>(R.id.etStation3).text.toString(),
            findViewById<EditText>(R.id.etStation4).text.toString()
        )

        val stationsCount = CreateTourDurationHelper.countFilledStations(stationTexts)
        val suggested = CreateTourDurationHelper.suggestDuration(stationsCount)

        val durationSpinner = findViewById<Spinner>(R.id.spinnerDuration)
        val options = resources.getStringArray(R.array.duration_options)
        val index = options.indexOf(suggested)

        if (index >= 0) {
            durationSpinner.setSelection(index)
        }
    }

    private fun saveTour() {
        val tourName = findViewById<EditText>(R.id.etTourName).text.toString().trim()
        val cityRaw = findViewById<AutoCompleteTextView>(R.id.etCity).text.toString().trim()
        val city = CityNormalizer.canonical(cityRaw)
        val description = findViewById<EditText>(R.id.etDescription).text.toString().trim()

        val mood = findViewById<Spinner>(R.id.spinnerMood).selectedItem.toString().trim()
        val timeFit = findViewById<Spinner>(R.id.spinnerTimeFit).selectedItem.toString().trim()
        val estimatedDuration = findViewById<Spinner>(R.id.spinnerDuration).selectedItem.toString().trim()

        val basicError = CreateTourHelper.validateBasicFields(tourName, city)
        if (basicError != null) {
            Toast.makeText(this, basicError, Toast.LENGTH_SHORT).show()
            return
        }

        val stationsList = mutableListOf<Station>()
        val ids = listOf(
            R.id.etStation1,
            R.id.etStation2,
            R.id.etStation3,
            R.id.etStation4
        )

        ids.forEachIndexed { index, id ->
            val text = findViewById<EditText>(id).text.toString().trim()

            if (text.isNotBlank()) {
                val selected = selectedStations[index]
                if (selected == null) {
                    Toast.makeText(
                        this,
                        "Please choose station ${index + 1} from Google search",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                stationsList.add(
                    Station(
                        name = selected.name,
                        query = selected.query,
                        latitude = selected.latLng.latitude,
                        longitude = selected.latLng.longitude
                    )
                )
            }
        }

        val stationsError = CreateTourHelper.validateStations(stationsList)
        if (stationsError != null) {
            Toast.makeText(this, stationsError, Toast.LENGTH_SHORT).show()
            return
        }

        val uid = AppGraph.auth.currentUser?.uid ?: ""

        lifecycleScope.launch {
            val newTour = CreateTourHelper.buildTour(
                name = tourName,
                city = city,
                description = description,
                mood = mood,
                timeTag = timeFit,
                duration = estimatedDuration,
                stations = stationsList,
                uid = uid
            )

            createTourUseCase.createTour(
                newTour,
                selectedImageUri,
                onSuccess = {
                    Toast.makeText(
                        this@CreateTourActivity,
                        "Route saved for approval! 🎉",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                },
                onError = { e ->
                    Toast.makeText(
                        this@CreateTourActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        }
    }
}