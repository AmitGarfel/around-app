package com.example.around.ui

import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.around.R
import com.example.around.di.AppGraph
import com.example.around.domain.model.Tour
import com.example.around.ui.base.BaseActivity
import com.example.around.ui.formatters.AdminMessageFormatter
import com.example.around.ui.helpers.AdminToursHelper

class AdminActivity : BaseActivity() {

    private val getPendingToursUseCase = AppGraph.getPendingToursUseCase
    private val updateTourStatusUseCase = AppGraph.updateTourStatusUseCase

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminTourAdapter
    private val pendingList: MutableList<Tour> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        setupBottomNav(R.id.nav_menu)
        setupRecyclerView()
        fetchPendingTours()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.rvPendingTours)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = AdminTourAdapter(pendingList) { tourId, isApproved ->
            handleTourAction(tourId, isApproved)
        }

        recyclerView.adapter = adapter
    }

    private fun handleTourAction(tourId: String, isApproved: Boolean) {
        val position = AdminToursHelper.findPositionByTourId(pendingList, tourId)
        if (position == -1) return

        updateTourStatus(
            tourId = tourId,
            newStatus = AdminToursHelper.toStatus(isApproved),
            position = position
        )
    }

    private fun fetchPendingTours() {
        getPendingToursUseCase(
            onSuccess = { list ->
                updatePendingTours(list)
            },
            onError = {
                showToast(AdminMessageFormatter.loadError())
            }
        )
    }

    private fun updatePendingTours(list: List<Tour>) {
        pendingList.clear()
        pendingList.addAll(list)
        adapter.notifyDataSetChanged()
    }

    private fun updateTourStatus(tourId: String, newStatus: String, position: Int) {
        updateTourStatusUseCase(
            tourId = tourId,
            newStatus = newStatus,
            onSuccess = {
                showToast(AdminMessageFormatter.statusUpdated(newStatus))
                removeTourFromList(position)
            },
            onError = {
                showToast(AdminMessageFormatter.updateFailed())
            }
        )
    }

    private fun removeTourFromList(position: Int) {
        val removed = AdminToursHelper.removeAt(pendingList, position)
        if (removed) {
            adapter.notifyItemRemoved(position)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}