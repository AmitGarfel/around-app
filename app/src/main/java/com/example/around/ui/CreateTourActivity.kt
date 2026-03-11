package com.example.around.ui

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.around.di.AppGraph
import com.example.around.R
import com.example.around.domain.model.Station
import com.example.around.domain.model.Tour

class CreateTourActivity : AppCompatActivity() {

    // ✅ במקום Firestore+Storage כאן
    private val createTourUseCase = AppGraph.createTourUseCase

    private var selectedImageUri: Uri? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                val preview = findViewById<ImageView>(R.id.ivPreview)
                preview.visibility = ImageView.VISIBLE
                preview.setImageURI(uri)
                Toast.makeText(this, "תמונה נבחרה ✅", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_tour)

        // ✅ חשוב: קודם כל להגדיר adapters של spinners
        setupSpinners()
        setupCityAutocomplete()
        setupAutoDurationByStations()

        findViewById<Button>(R.id.btnPickImage).setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        findViewById<Button>(R.id.btnSaveRoute).setOnClickListener {
            saveTour()
        }

        findViewById<Button>(R.id.btnExit).setOnClickListener {
            finish()
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

        val cities = resources.getStringArray(R.array.cities_options).toList()
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            cities
        )

        cityAuto.setAdapter(adapter)
        cityAuto.threshold = 1
        cityAuto.setOnClickListener { cityAuto.showDropDown() }
    }

    private fun setupAutoDurationByStations() {
        val s1 = findViewById<EditText>(R.id.etStation1)
        val s2 = findViewById<EditText>(R.id.etStation2)
        val s3 = findViewById<EditText>(R.id.etStation3)
        val s4 = findViewById<EditText>(R.id.etStation4)
        val durationSpinner = findViewById<Spinner>(R.id.spinnerDuration)

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateDurationDefault(durationSpinner)
            }
        }

        s1.addTextChangedListener(watcher)
        s2.addTextChangedListener(watcher)
        s3.addTextChangedListener(watcher)
        s4.addTextChangedListener(watcher)

        updateDurationDefault(durationSpinner)
    }

    private fun updateDurationDefault(durationSpinner: Spinner) {
        val stationsCount = countFilledStations()

        val suggested = when (stationsCount) {
            0, 1, 2 -> "30–60 min"
            3, 4 -> "1–1.5 hours"
            else -> "1.5–2 hours"
        }

        val options = resources.getStringArray(R.array.duration_options)
        val index = options.indexOf(suggested)
        if (index >= 0 && durationSpinner.selectedItemPosition != index) {
            durationSpinner.setSelection(index)
        }
    }

    private fun countFilledStations(): Int {
        val ids = listOf(R.id.etStation1, R.id.etStation2, R.id.etStation3, R.id.etStation4)
        return ids.count { id ->
            findViewById<EditText>(id).text.toString().trim().isNotEmpty()
        }
    }

    private fun saveTour() {
        val tourName = findViewById<EditText>(R.id.etTourName).text.toString().trim()
        val city = findViewById<AutoCompleteTextView>(R.id.etCity).text.toString().trim()
        val description = findViewById<EditText>(R.id.etDescription).text.toString().trim()

        val mood = findViewById<Spinner>(R.id.spinnerMood).selectedItem.toString().trim()
        val timeFit = findViewById<Spinner>(R.id.spinnerTimeFit).selectedItem.toString().trim()
        val estimatedDuration =
            findViewById<Spinner>(R.id.spinnerDuration).selectedItem.toString().trim()

        val s1 = findViewById<EditText>(R.id.etStation1).text.toString().trim()
        val s2 = findViewById<EditText>(R.id.etStation2).text.toString().trim()
        val s3 = findViewById<EditText>(R.id.etStation3).text.toString().trim()
        val s4 = findViewById<EditText>(R.id.etStation4).text.toString().trim()

        val stationsList = listOf(s1, s2, s3, s4)
            .filter { it.isNotEmpty() }
            .map { Station(name = it, query = it) }

        if (tourName.isEmpty() || city.isEmpty()) {
            Toast.makeText(this, "עמיתוש, חייב למלא שם ועיר!", Toast.LENGTH_SHORT).show()
            return
        }

        // ✅ יוצרים Tour בלי URL (ה-UseCase יעדכן אם יש תמונה)
        val newTour = Tour(
            name = tourName,
            city = city,
            description = description,
            mood = mood,
            timeTag = timeFit,
            estimatedDuration = estimatedDuration,
            status = "pending",
            likesCount = 0,
            imageUrl = "",
            stations = stationsList
        )

        val imageUri = selectedImageUri
        if (imageUri != null) {
            Toast.makeText(this, "מעלה תמונה... ⏫", Toast.LENGTH_SHORT).show()
        }

        createTourUseCase.createTour(
            tour = newTour,
            imageUri = imageUri,
            onSuccess = {
                Toast.makeText(this, "המסלול נשלח לאישור המערכת!", Toast.LENGTH_LONG).show()
                finish()
            },
            onError = { e ->
                Log.e("CREATE_TOUR", "Create failed", e)
                Toast.makeText(this, "שגיאה בשמירה: ${e.localizedMessage}", Toast.LENGTH_LONG)
                    .show()
            }
        )
    }
}