package com.example.around.ui

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.around.R
import com.example.around.domain.model.Station

class StationsAdapter(
    private val stations: List<Station>,
    private val onClick: (Station) -> Unit
) : RecyclerView.Adapter<StationsAdapter.StationVH>() {

    private var currentStationIndex: Int = 0

    class StationVH(view: View) : RecyclerView.ViewHolder(view) {
        val leftIndicator: View = view.findViewById(R.id.viewCurrentIndicator)
        val tvIndex: TextView = view.findViewById(R.id.tvStationIndex)
        val tvName: TextView = view.findViewById(R.id.tvStationName)
        val tvBadge: TextView = view.findViewById(R.id.tvStationBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationVH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_station, parent, false)
        return StationVH(v)
    }

    override fun onBindViewHolder(holder: StationVH, position: Int) {
        val context = holder.itemView.context
        val station = stations[position]

        val isCurrent = position == currentStationIndex
        val isPassed = position < currentStationIndex

        holder.tvName.text = station.name.ifBlank { "Station ${position + 1}" }

        when {
            isCurrent -> {
                holder.leftIndicator.visibility = View.VISIBLE
                holder.tvIndex.text = (position + 1).toString()
                holder.tvIndex.setTextColor(ContextCompat.getColor(context, R.color.black))
                holder.tvName.setTypeface(null, Typeface.BOLD)
                holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.black))
                holder.tvBadge.visibility = View.VISIBLE
                holder.tvBadge.text = "Current"
            }

            isPassed -> {
                holder.leftIndicator.visibility = View.INVISIBLE
                holder.tvIndex.text = "✓"
                holder.tvIndex.setTextColor(
                    ContextCompat.getColor(context, android.R.color.darker_gray)
                )
                holder.tvName.setTypeface(null, Typeface.NORMAL)
                holder.tvName.setTextColor(
                    ContextCompat.getColor(context, android.R.color.darker_gray)
                )
                holder.tvBadge.visibility = View.GONE
            }

            else -> {
                holder.leftIndicator.visibility = View.INVISIBLE
                holder.tvIndex.text = (position + 1).toString()
                holder.tvIndex.setTextColor(ContextCompat.getColor(context, R.color.black))
                holder.tvName.setTypeface(null, Typeface.NORMAL)
                holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.black))
                holder.tvBadge.visibility = View.GONE
            }
        }

        holder.itemView.setOnClickListener { onClick(station) }
    }

    override fun getItemCount(): Int = stations.size

    fun updateCurrentStation(index: Int) {
        if (stations.isEmpty()) return
        currentStationIndex = index.coerceIn(0, stations.lastIndex)
        notifyDataSetChanged()
    }
}