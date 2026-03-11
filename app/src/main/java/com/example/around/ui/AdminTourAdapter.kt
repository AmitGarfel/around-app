package com.example.around.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.around.R
import com.example.around.domain.model.Tour

class AdminTourAdapter(
    private val tourList: MutableList<Tour>,
    private val onAction: (tourId: String, isApproved: Boolean) -> Unit
) : RecyclerView.Adapter<AdminTourAdapter.AdminViewHolder>() {

    class AdminViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvPendingName)
        val meta: TextView = view.findViewById(R.id.tvPendingMeta)
        val duration: TextView = view.findViewById(R.id.tvPendingDuration)
        val desc: TextView = view.findViewById(R.id.tvPendingDesc)
        val image: ImageView = view.findViewById(R.id.ivPendingImage)
        val btnViewDetails: View = view.findViewById(R.id.btnViewDetails)
        val btnApprove: Button = view.findViewById(R.id.btnApprove)
        val btnReject: Button = view.findViewById(R.id.btnReject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pending_tour, parent, false)
        return AdminViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminViewHolder, position: Int) {
        val tour = tourList[position]
        val context = holder.itemView.context

        holder.name.text = tour.name
        holder.meta.text = "${tour.city} • ${tour.mood} • ${tour.timeTag}"

        val durationText = tour.estimatedDuration.ifBlank { "—" }
        val stationsCount = tour.stations.size
        val stationsText = if (stationsCount == 0) "no stations" else "$stationsCount stations"
        holder.duration.text = "$durationText • $stationsText"

        holder.desc.text = tour.description.ifBlank { "No description" }

        if (tour.imageUrl.isNotBlank()) {
            Glide.with(context)
                .load(tour.imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.stat_notify_error)
                .into(holder.image)
        } else {
            holder.image.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        holder.btnViewDetails.setOnClickListener {
            val activity = context as? AppCompatActivity ?: return@setOnClickListener
            TourDetailsBottomSheet.newInstance(tour)
                .show(activity.supportFragmentManager, "admin_tour_details")
        }

        holder.btnApprove.setOnClickListener { onAction(tour.id, true) }
        holder.btnReject.setOnClickListener { onAction(tour.id, false) }
    }

    override fun getItemCount(): Int = tourList.size

    fun removeAt(position: Int) {
        if (position < 0 || position >= tourList.size) return
        tourList.removeAt(position)
        notifyItemRemoved(position)
    }
}