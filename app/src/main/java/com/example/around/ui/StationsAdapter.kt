package com.example.around.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.around.R
import com.example.around.domain.model.Station

class StationsAdapter(
    private val stations: List<Station>,
    private val onClick: (Station) -> Unit
) : RecyclerView.Adapter<StationsAdapter.StationVH>() {

    class StationVH(view: View) : RecyclerView.ViewHolder(view) {
        val tvIndex: TextView = view.findViewById(R.id.tvStationIndex)
        val tvName: TextView = view.findViewById(R.id.tvStationName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationVH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_station, parent, false)
        return StationVH(v)
    }

    override fun onBindViewHolder(holder: StationVH, position: Int) {
        val station = stations[position]
        holder.tvIndex.text = (position + 1).toString()
        holder.tvName.text = station.name
        holder.itemView.setOnClickListener { onClick(station) }
    }

    override fun getItemCount(): Int = stations.size
}