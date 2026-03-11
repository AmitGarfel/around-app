package com.example.around.ui

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.around.R
import com.example.around.di.AppGraph

class TourListActivity : AppCompatActivity() {

    private val loadToursUseCase = AppGraph.loadToursWithLikesUseCase
    private val likesRepo = AppGraph.likesRepo
    private val auth = AppGraph.auth

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tour_list)

        val mood = intent.getStringExtra("MOOD") ?: "culinary"
        val time = intent.getStringExtra("TIME") ?: "Evening"

        findViewById<TextView>(R.id.listTitle).text = "$mood routes"
        findViewById<TextView>(R.id.listSubtitle).text = "Perfect for $time ✨"

        recyclerView = findViewById(R.id.toursRecyclerView)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchTours(mood, time)
    }

    private fun fetchTours(mood: String, time: String) {
        loadToursUseCase.load(
            mood = mood,
            time = time,
            onSuccess = { tours ->

                if (tours.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    emptyStateLayout.visibility = View.VISIBLE
                    return@load
                } else {
                    recyclerView.visibility = View.VISIBLE
                    emptyStateLayout.visibility = View.GONE
                }

                val userId = auth.currentUser?.uid

                recyclerView.adapter =
                    TourAdapter(tours) { tourItem, prevLiked, prevCount, doneUi ->

                        if (userId == null) {
                            doneUi()
                            return@TourAdapter
                        }

                        likesRepo.toggleLike(
                            tourId = tourItem.id,
                            onDone = { isLikedNow, finalCount ->
                                tourItem.isLikedByMe = isLikedNow
                                if (finalCount != null) tourItem.likesCount = finalCount
                                doneUi()
                            },
                            onError = {
                                tourItem.isLikedByMe = prevLiked
                                tourItem.likesCount = prevCount
                                doneUi()
                            }
                        )
                    }
            },
            onError = {
                Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
