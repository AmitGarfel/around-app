package com.example.around.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.around.R
import com.example.around.domain.model.Tour
import com.example.around.ui.formatters.TourUiFormatter
import com.example.around.ui.helpers.TourLikeUiHelper

class TourAdapter(
    private var tourList: List<Tour>,
    private val onLikeClick: (tour: Tour, prevLiked: Boolean, prevCount: Int, doneUi: () -> Unit) -> Unit
) : RecyclerView.Adapter<TourAdapter.TourViewHolder>() {

    class TourViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tourNameTv)
        val details: TextView = view.findViewById(R.id.tourDetailsTv)
        val likes: TextView = view.findViewById(R.id.likesCountTv)
        val image: ImageView = view.findViewById(R.id.tourImage)
        val btnView: View = view.findViewById(R.id.btnView)
        val btnLike: ImageView = view.findViewById(R.id.btnLike)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TourViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tour, parent, false)
        return TourViewHolder(view)
    }

    override fun onBindViewHolder(holder: TourViewHolder, position: Int) {
        val tour = tourList[position]
        val context = holder.itemView.context

        holder.name.text = tour.name
        holder.likes.text = tour.likesCount.toString()
        holder.details.text = TourUiFormatter.buildDetails(tour)

        Glide.with(context)
            .load(tour.imageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.stat_notify_error)
            .into(holder.image)

        updateLikeUI(holder.btnLike, tour.isLikedByMe)

        holder.btnLike.setOnClickListener {
            holder.btnLike.isEnabled = false

            val previousState = TourLikeUiHelper.applyOptimisticToggle(tour)

            updateLikeUI(holder.btnLike, tour.isLikedByMe)
            holder.likes.text = tour.likesCount.toString()

            onLikeClick(
                tour,
                previousState.wasLiked,
                previousState.likesCount
            ) {
                holder.btnLike.isEnabled = true
                updateLikeUI(holder.btnLike, tour.isLikedByMe)
                holder.likes.text = tour.likesCount.toString()
            }
        }

        holder.btnView.setOnClickListener {
            val activity = context as? AppCompatActivity
            activity?.let {
                TourDetailsBottomSheet
                    .newInstance(tour)
                    .show(it.supportFragmentManager, "tour_details")
            }
        }
    }

    private fun updateLikeUI(imageView: ImageView, isLiked: Boolean) {
        imageView.setImageResource(
            if (isLiked) R.drawable.heart_full else R.drawable.heart_outline
        )
    }

    override fun getItemCount(): Int = tourList.size
}