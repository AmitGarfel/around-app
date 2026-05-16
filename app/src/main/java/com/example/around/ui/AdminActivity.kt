package com.example.around.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
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
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var btnBack: ImageButton
    private lateinit var adapter: AdminTourAdapter

    private val pendingList = mutableListOf<Tour>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        setupBottomNav(R.id.nav_menu)
        setupViews()
        setupRecyclerView()
        setupBackButton()
        fetchPendingTours()
    }

    override fun onResume() {
        super.onResume()
        refreshBottomNavSelection(R.id.nav_menu)
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.rvPendingTours)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = AdminTourAdapter(pendingList) { tourId, isApproved ->
            handleTourAction(tourId, isApproved)
        }

        recyclerView.adapter = adapter
    }

    private fun setupBackButton() {
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun fetchPendingTours() {
        getPendingToursUseCase(
            onSuccess = { tours ->
                updatePendingTours(tours)
            },
            onError = {
                showToast(AdminMessageFormatter.loadError())
            }
        )
    }

    private fun handleTourAction(tourId: String, isApproved: Boolean) {
        val position = AdminToursHelper.findPositionByTourId(pendingList, tourId)
        if (position == -1) return

        val newStatus = AdminToursHelper.toStatus(isApproved)

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

    private fun updatePendingTours(newList: List<Tour>) {
        val oldSize = pendingList.size

        pendingList.clear()
        if (oldSize > 0) {
            adapter.notifyItemRangeRemoved(0, oldSize)
        }

        pendingList.addAll(newList)
        if (newList.isNotEmpty()) {
            adapter.notifyItemRangeInserted(0, newList.size)
        }

        updateEmptyState()
    }

    private fun removeTourFromList(position: Int) {
        val removed = AdminToursHelper.removeAt(pendingList, position)
        if (!removed) return

        adapter.notifyItemRemoved(position)
        adapter.notifyItemRangeChanged(position, pendingList.size - position)
        updateEmptyState()
    }

    private fun updateEmptyState() {
        if (pendingList.isEmpty()) {
            emptyStateLayout.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyStateLayout.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}