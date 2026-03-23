package com.example.around.ui.helpers

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.around.domain.model.Station
import com.example.around.ui.StationsAdapter

class StationsListBinder {

    fun bind(
        recyclerView: RecyclerView,
        stations: List<Station>,
        onStationClick: (Station) -> Unit
    ): StationsAdapter {

        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.isNestedScrollingEnabled = false

        val adapter = StationsAdapter(stations, onStationClick)
        recyclerView.adapter = adapter

        return adapter
    }
}