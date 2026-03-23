package com.example.around.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.around.R
import com.example.around.data.geo.LocationDistanceUtils
import com.example.around.data.geo.LocationHelper
import com.example.around.di.AppGraph
import com.example.around.domain.model.Tour
import com.example.around.ui.base.BaseActivity
import com.example.around.ui.helpers.TourLikeUiHelper
import com.example.around.util.NavigationKeys

class TourListActivity : BaseActivity() {

    private val loadToursUseCase = AppGraph.loadToursWithLikesUseCase
    private val likesRepo = AppGraph.likesRepo
    private val auth = AppGraph.auth

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var locationHelper: LocationHelper

    private lateinit var mood: String
    private lateinit var time: String
    private lateinit var selectedCity: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tour_list)

        setupBackButton()

        mood = intent.getStringExtra(NavigationKeys.EXTRA_MOOD) ?: "culinary"
        time = intent.getStringExtra(NavigationKeys.EXTRA_TIME) ?: "Evening"
        selectedCity = intent.getStringExtra(NavigationKeys.EXTRA_CITY) ?: "Tel Aviv"

        recyclerView = findViewById(R.id.toursRecyclerView)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
        locationHelper = LocationHelper(this)

        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<TextView>(R.id.listTitle).text =
            mood.replaceFirstChar { it.uppercase() } + " routes"
        findViewById<TextView>(R.id.listSubtitle).text =
            "Perfect for $time in $selectedCity ✨"

        fetchTours()
    }

    private fun setupBackButton() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    private fun fetchTours() {
        loadToursUseCase.load(
            mood = mood,
            time = time,
            city = selectedCity,
            onSuccess = { tours ->
                if (tours.isEmpty()) {
                    showTours(emptyList())
                    return@load
                }

                sortAndShowTours(tours)
            },
            onError = { e ->
                Toast.makeText(
                    this,
                    "Error fetching tours: ${e.localizedMessage ?: "unknown error"}",
                    Toast.LENGTH_SHORT
                ).show()
                showTours(emptyList())
            }
        )
    }

    private fun sortAndShowTours(tours: List<Tour>) {
        if (!hasLocationPermission()) {
            showTours(tours)
            return
        }

        locationHelper.getCurrentLatLng { userLatLng ->
            if (userLatLng == null) {
                showTours(tours)
                return@getCurrentLatLng
            }

            val sortedTours = tours.sortedBy { tour ->
                if (tour.startLatitude == 0.0 && tour.startLongitude == 0.0) {
                    Float.MAX_VALUE
                } else {
                    LocationDistanceUtils.distanceInKm(
                        fromLat = userLatLng.latitude,
                        fromLng = userLatLng.longitude,
                        toLat = tour.startLatitude,
                        toLng = tour.startLongitude
                    )
                }
            }

            showTours(sortedTours)
        }
    }

    private fun showTours(tours: List<Tour>) {
        if (tours.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyStateLayout.visibility = View.VISIBLE
            recyclerView.adapter = null
            return
        }

        recyclerView.visibility = View.VISIBLE
        emptyStateLayout.visibility = View.GONE

        val userId = auth.currentUser?.uid

        recyclerView.adapter = TourAdapter(tours) { tourItem, previousState, doneUi ->
            if (userId == null) {
                TourLikeUiHelper.restorePreviousState(tourItem, previousState)
                doneUi()
                return@TourAdapter
            }

            likesRepo.toggleLike(
                tourId = tourItem.id,
                onDone = { isLikedNow, finalCount ->
                    tourItem.isLikedByMe = isLikedNow
                    if (finalCount != null) {
                        tourItem.likesCount = finalCount
                    }
                    doneUi()
                },
                onError = {
                    TourLikeUiHelper.restorePreviousState(tourItem, previousState)
                    doneUi()
                }
            )
        }
    }

    private fun hasLocationPermission(): Boolean {
        val fineGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineGranted || coarseGranted
    }
}