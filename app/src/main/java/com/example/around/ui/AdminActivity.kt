package com.example.around.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.around.ui.AdminTourAdapter
import com.example.around.di.AppGraph
import com.example.around.R
import com.example.around.domain.model.Tour

class AdminActivity : AppCompatActivity() {

    // ✅ במקום FirebaseFirestore בתוך ה-Activity
    private val toursRepo = AppGraph.toursRepo

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminTourAdapter
    private val pendingList: MutableList<Tour> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        recyclerView = findViewById(R.id.rvPendingTours)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = AdminTourAdapter(pendingList) { tourId, isApproved ->
            val pos = pendingList.indexOfFirst { it.id == tourId }
            if (pos == -1) return@AdminTourAdapter
            updateTourStatus(tourId, isApproved, pos)
        }

        recyclerView.adapter = adapter

        fetchPendingTours()
    }

    private fun fetchPendingTours() {
        toursRepo.getPendingTours(
            onSuccess = { list ->
                pendingList.clear()
                pendingList.addAll(list)
                adapter.notifyDataSetChanged()
            },
            onError = {
                Toast.makeText(this, "שגיאה בטעינת מסלולים", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun updateTourStatus(tourId: String, isApproved: Boolean, position: Int) {
        val newStatus = if (isApproved) "approved" else "rejected"

        toursRepo.updateStatus(
            tourId = tourId,
            newStatus = newStatus,
            onSuccess = {
                Toast.makeText(this, "הסטטוס עודכן ל-$newStatus", Toast.LENGTH_SHORT).show()
                adapter.removeAt(position)
            },
            onError = {
                Toast.makeText(this, "עדכון סטטוס נכשל", Toast.LENGTH_SHORT).show()
            }
        )
    }
}