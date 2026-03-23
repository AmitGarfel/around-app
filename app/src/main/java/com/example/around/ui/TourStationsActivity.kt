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
import com.example.around.ui.formatters.TourStationsUiFormatter
import com.example.around.ui.helpers.TourProgressManager
import com.example.around.ui.helpers.TravelModeMapper
import com.example.around.util.NavigationKeys
import com.example.around.util.PlaceQueryBuilder
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

    private val loadTourStationsUseCase = AppGraph.loadTourStationsUseCase
    private lateinit var geocodingRepo: GeocodingRepository
    private lateinit var navRepo: MapsNavigationRepository
    private lateinit var stationsAdapter: StationsAdapter

    private var mapRenderer: MapRenderer? = null

    private lateinit var tourId: String
    private var stations: List<Station> = emptyList()
    private var tourName: String = ""
    private var tourCity: String = ""

    private var selectedTravelMode = TravelModeMapper.fromSpinnerPosition(0)
    private var progressManager: TourProgressManager? = null

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
        setupTravelModeSpinner()

        val incomingId = intent.getStringExtra(NavigationKeys.EXTRA_TOUR_ID)
        if (incomingId.isNullOrBlank()) {
            Toast.makeText(
                this,
                TourStationsUiFormatter.missingTourId(),
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }
        tourId = incomingId

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
        gMap.uiSettings.isMapToolbarEnabled = true

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
                MotionEvent.ACTION_POINTER_DOWN,
                MotionEvent.ACTION_POINTER_UP -> {
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
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedTravelMode = TravelModeMapper.fromSpinnerPosition(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedTravelMode = TravelModeMapper.fromSpinnerPosition(0)
            }
        }
    }

    private fun loadTour(tourId: String) {
        loadTourStationsUseCase.load(
            tourId = tourId,
            onSuccess = { data ->
                tourName = data.tourName
                tourCity = data.city
                stations = data.stations
                progressManager = TourProgressManager(stations.size)

                resolvedPositions = MutableList(stations.size) { null }

                findViewById<TextView>(R.id.tvTourTitle).text = tourName
                findViewById<TextView>(R.id.tvSubTitle).text =
                    TourStationsUiFormatter.buildSubtitle(tourCity)

                setupStationsList(stations)
                renderStationsOnMapIfReady()
            },
            onError = { e ->
                Toast.makeText(
                    this,
                    TourStationsUiFormatter.loadFailed(e.localizedMessage),
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        )
    }

    private fun setupStationsList(stations: List<Station>) {
        val rv = findViewById<RecyclerView>(R.id.rvStations)
        rv.layoutManager = LinearLayoutManager(this)
        rv.isNestedScrollingEnabled = false

        stationsAdapter = StationsAdapter(stations) { station ->
            navigateToStation(station)
        }

        rv.adapter = stationsAdapter
        stationsAdapter.updateCurrentStation(progressManager?.currentIndex() ?: 0)

        findViewById<View>(R.id.btnNavigate).setOnClickListener {
            navigateToStationByIndex(progressManager?.currentIndex() ?: 0)
        }

        val btnNextStation = findViewById<View?>(R.id.btnNextStation)
        btnNextStation?.setOnClickListener {
            moveToNextStation()
        }
    }

    private fun renderStationsOnMapIfReady() {
        val renderer = mapRenderer ?: return
        if (stations.isEmpty()) return
        if (isResolving) return

        isResolving = true

        ioScope.launch {
            for (i in stations.indices) {
                val station = stations[i]

                resolvedPositions[i] = if (hasSavedCoordinates(station)) {
                    LatLng(station.latitude, station.longitude)
                } else {
                    geocodeStation(station)
                }

                Log.d("MAP_DEBUG", "Resolved ${station.name} -> ${resolvedPositions[i]}")
            }

            withContext(Dispatchers.Main) {
                renderer.renderStations(stations, resolvedPositions)
                isResolving = false
            }
        }
    }

    private fun hasSavedCoordinates(station: Station): Boolean {
        return station.latitude != 0.0 && station.longitude != 0.0
    }

    private suspend fun geocodeStation(station: Station): LatLng? {
        if (hasSavedCoordinates(station)) {
            return LatLng(station.latitude, station.longitude)
        }

        val queryText = PlaceQueryBuilder.build(station, tourCity)
        if (queryText.isBlank()) {
            Log.d("MAP_DEBUG", "Blank query for ${station.name}")
            return null
        }

        Log.d("MAP_DEBUG", "Geocoding query for ${station.name}: $queryText")
        return geocodingRepo.geocode(queryText)
    }

    private fun navigateToStation(station: Station) {
        val searchText = PlaceQueryBuilder.build(station, tourCity)

        if (selectedTravelMode.dirMode == "transit") {
            if (searchText.isBlank()) {
                Toast.makeText(
                    this,
                    TourStationsUiFormatter.destinationNotFound(),
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            navRepo.openTransitDirections(searchText)
            return
        }

        if (searchText.isNotBlank()) {
            navRepo.navigateToSearchPlace(searchText, selectedTravelMode.navMode)
            return
        }

        if (hasSavedCoordinates(station)) {
            navRepo.navigateToLatLng(
                latitude = station.latitude,
                longitude = station.longitude,
                label = station.name,
                navMode = selectedTravelMode.navMode
            )
            return
        }

        Toast.makeText(this, "Destination not found", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToStationByIndex(index: Int) {
        val manager = progressManager

        if (manager == null || !manager.isValidIndex(index)) {
            Toast.makeText(
                this,
                TourStationsUiFormatter.noMoreStations(),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val station = stations[index]
        navigateToStation(station)
    }

    private fun moveToNextStation() {
        val manager = progressManager

        if (manager == null || !manager.hasStations()) {
            Toast.makeText(
                this,
                TourStationsUiFormatter.noStationsFound(),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val moved = manager.moveNext()

        if (moved) {
            val newIndex = manager.currentIndex()

            stationsAdapter.updateCurrentStation(newIndex)
            findViewById<RecyclerView>(R.id.rvStations).smoothScrollToPosition(newIndex)

            val stationName = stations[newIndex].name
            Toast.makeText(
                this,
                TourStationsUiFormatter.currentStop(stationName),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this,
                TourStationsUiFormatter.reachedLastStation(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}