package com.example.around.ui

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.around.R
import com.example.around.data.geo.GeocodingRepository
import com.example.around.data.geo.MapRenderer
import com.example.around.data.geo.MapsNavigationRepository
import com.example.around.di.AppGraph
import com.example.around.domain.model.Station
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TourStationsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var gMap: GoogleMap

    private val toursRepo = AppGraph.toursRepo
    private lateinit var geocodingRepo: GeocodingRepository
    private lateinit var navRepo: MapsNavigationRepository

    private var mapRenderer: MapRenderer? = null

    private lateinit var tourId: String
    private var stations: List<Station> = emptyList()
    private var tourName: String = ""
    private var tourCity: String = ""

    private var selectedTravelModeDir = "driving"
    private var selectedTravelModeNav = "d"

    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var resolvedPositions: MutableList<LatLng?> = mutableListOf()
    private var isResolving = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tour_stations)

        geocodingRepo = AppGraph.geocodingRepo(this)
        navRepo = AppGraph.mapsNavRepo(this)

        setupBackButton()
        setupMapTouchInScroll()

        val incomingId = intent.getStringExtra(EXTRA_TOUR_ID)
        if (incomingId.isNullOrBlank()) {
            Toast.makeText(this, "Missing tour id", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        tourId = incomingId

        setupTravelModeSpinner()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        loadTour(tourId)
    }

    override fun onDestroy() {
        super.onDestroy()
        ioScope.cancel()
    }

    override fun onMapReady(map: GoogleMap) {
        gMap = map

        gMap.uiSettings.isZoomGesturesEnabled = true
        gMap.uiSettings.isScrollGesturesEnabled = true
        gMap.uiSettings.isRotateGesturesEnabled = true
        gMap.uiSettings.isTiltGesturesEnabled = true
        gMap.uiSettings.isZoomControlsEnabled = true

        mapRenderer = MapRenderer(gMap)
        renderStationsOnMapIfReady()
    }

    private fun setupBackButton() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    private fun setupMapTouchInScroll() {
        val scrollView = findViewById<ScrollView>(R.id.stationsScroll)
        val mapTouchLayer = findViewById<View>(R.id.mapTouchLayer)

        mapTouchLayer.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN,
                MotionEvent.ACTION_MOVE,
                MotionEvent.ACTION_POINTER_DOWN -> {
                    scrollView.requestDisallowInterceptTouchEvent(true)
                }

                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {
                    scrollView.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }
    }

    private fun setupTravelModeSpinner() {
        val spinner = findViewById<Spinner>(R.id.spinnerTravelMode)

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.travel_modes,
            android.R.layout.simple_list_item_1
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(0)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> {
                        selectedTravelModeDir = "driving"
                        selectedTravelModeNav = "d"
                    }
                    1 -> {
                        selectedTravelModeDir = "walking"
                        selectedTravelModeNav = "w"
                    }
                    2 -> {
                        selectedTravelModeDir = "bicycling"
                        selectedTravelModeNav = "b"
                    }
                    3 -> {
                        selectedTravelModeDir = "transit"
                        selectedTravelModeNav = "r"
                    }
                    else -> {
                        selectedTravelModeDir = "driving"
                        selectedTravelModeNav = "d"
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) = Unit
        }
    }

    private fun loadTour(tourId: String) {
        toursRepo.getTourById(
            tourId = tourId,
            onSuccess = { tour ->
                tourName = tour.name
                tourCity = tour.city
                stations = tour.stations

                resolvedPositions = MutableList(stations.size) { null }

                findViewById<TextView>(R.id.tvTourTitle).text = tourName
                findViewById<TextView>(R.id.tvSubTitle).text =
                    "Follow the stations and start exploring in $tourCity ✨"

                setupStationsList(stations)
                renderStationsOnMapIfReady()
            },
            onError = { e ->
                Toast.makeText(this, "Load failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                finish()
            }
        )
    }

    private fun setupStationsList(stations: List<Station>) {
        val rv = findViewById<RecyclerView>(R.id.rvStations)
        rv.layoutManager = LinearLayoutManager(this)

        rv.adapter = StationsAdapter(stations) { station ->
            navigateToStationByName(station)
        }

        findViewById<View>(R.id.btnNavigate).setOnClickListener {
            navigateWholeTourByNames()
        }
    }

    private fun renderStationsOnMapIfReady() {
        val renderer = mapRenderer ?: return
        if (stations.isEmpty()) return
        if (isResolving) return

        if (resolvedPositions.size != stations.size) {
            resolvedPositions = MutableList(stations.size) { null }
        }

        isResolving = true

        ioScope.launch {
            for (i in stations.indices) {
                val latLng = geocodeStation(stations[i])
                resolvedPositions[i] = latLng
            }

            withContext(Dispatchers.Main) {
                renderer.renderStations(stations, resolvedPositions)
                isResolving = false
            }
        }
    }

    private suspend fun geocodeStation(station: Station): LatLng? {
        val base = station.query.trim().ifBlank { station.name.trim() }
        if (base.isBlank()) return null

        val city = tourCity.trim()
        val queryText =
            if (city.isNotBlank() && !base.contains(city, ignoreCase = true)) {
                "$base, $city"
            } else {
                base
            }

        return geocodingRepo.geocode(queryText)
    }

    private fun navigateToStationByName(station: Station) {
        val base = station.query.trim().ifBlank { station.name.trim() }
        val city = tourCity.trim()

        val searchText = when {
            base.isBlank() -> ""
            city.isNotBlank() && !base.contains(city, ignoreCase = true) -> "$base, $city"
            else -> base
        }

        if (searchText.isBlank()) {
            Toast.makeText(this, "Destination not found", Toast.LENGTH_SHORT).show()
            return
        }

        navRepo.navigateToSearchPlace(searchText, selectedTravelModeNav)
    }

    private fun navigateWholeTourByNames() {
        if (stations.size < 2) {
            Toast.makeText(this, "Need at least 2 stations for a route", Toast.LENGTH_SHORT).show()
            return
        }

        val city = tourCity.trim()

        val points = stations.mapNotNull { station ->
            val base = station.query.trim().ifBlank { station.name.trim() }
            if (base.isBlank()) return@mapNotNull null
            if (city.isNotBlank() && !base.contains(city, ignoreCase = true)) {
                "$base, $city"
            } else {
                base
            }
        }

        if (points.size < 2) {
            Toast.makeText(this, "Couldn't build route points", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("NAV_NAMES", "mode=$selectedTravelModeDir points=$points")
        navRepo.navigateRouteByNames(points, selectedTravelModeDir)
    }

    companion object {
        const val EXTRA_TOUR_ID = "EXTRA_TOUR_ID"
    }
}