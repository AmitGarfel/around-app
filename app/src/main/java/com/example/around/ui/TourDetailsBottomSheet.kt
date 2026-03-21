package com.example.around.ui

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.around.R
import com.example.around.domain.model.Tour
import com.example.around.util.NavigationKeys
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TourDetailsBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.bottomsheet_tour_details, container, false)

        val name = requireArguments().getString(ARG_NAME) ?: ""
        val desc = requireArguments().getString(ARG_DESC) ?: ""
        val imageUrl = requireArguments().getString(ARG_IMAGE) ?: ""
        val tourId = requireArguments().getString(ARG_TOUR_ID) ?: ""
        val stations = requireArguments().getStringArrayList(ARG_STATIONS) ?: arrayListOf()

        v.findViewById<TextView>(R.id.tvTourDetailName).text = name
        v.findViewById<TextView>(R.id.tvTourDetailDesc).text = desc

        val stationsContainer = v.findViewById<LinearLayout>(R.id.llStationsContainer)
        stationsContainer.removeAllViews()
        stationsContainer.layoutDirection = View.LAYOUT_DIRECTION_LTR
        stationsContainer.gravity = Gravity.LEFT

        if (stations.isNotEmpty()) {
            stationsContainer.addView(makeStationsTitle("Stations"))

            stations.forEach { stationName ->
                if (stationName.isBlank()) return@forEach
                stationsContainer.addView(makeStationRow(stationName))
            }
        }

        val img = v.findViewById<ImageView>(R.id.ivTourDetailImage)
        if (imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.stat_notify_error)
                .into(img)
        } else {
            img.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        v.findViewById<View>(R.id.btnNotNow).setOnClickListener { dismiss() }

        v.findViewById<View>(R.id.btnLetsGo).setOnClickListener {
            if (tourId.isBlank()) {
                dismiss()
                return@setOnClickListener
            }

            val intent = Intent(requireContext(), TourStationsActivity::class.java)
            intent.putExtra(NavigationKeys.EXTRA_TOUR_ID, tourId)
            startActivity(intent)
            dismiss()
        }

        return v
    }

    private fun makeStationsTitle(text: String): TextView {
        return TextView(requireContext()).apply {
            this.text = text
            textSize = 13f
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(0xFF2D2321.toInt())

            layoutDirection = View.LAYOUT_DIRECTION_LTR
            gravity = Gravity.LEFT
            textAlignment = View.TEXT_ALIGNMENT_VIEW_START

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(8)
            }
        }
    }

    private fun makeStationRow(stationName: String): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL

            layoutDirection = View.LAYOUT_DIRECTION_LTR
            gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            setPadding(0, dp(6), 0, dp(6))

            val pin = TextView(requireContext()).apply {
                text = "📍"
                textSize = 14f

                layoutDirection = View.LAYOUT_DIRECTION_LTR
                gravity = Gravity.LEFT

                layoutParams = LinearLayout.LayoutParams(
                    dp(22),
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val nameTv = TextView(requireContext()).apply {
                text = stationName
                textSize = 14f
                setTextColor(0xFF2D2321.toInt())

                layoutDirection = View.LAYOUT_DIRECTION_LTR
                gravity = Gravity.LEFT
                textAlignment = View.TEXT_ALIGNMENT_VIEW_START

                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }

            addView(pin)
            addView(nameTv)
        }
    }

    private fun dp(value: Int): Int {
        val density = resources.displayMetrics.density
        return (value * density).toInt()
    }

    companion object {
        private const val ARG_NAME = "name"
        private const val ARG_DESC = "desc"
        private const val ARG_IMAGE = "image"
        private const val ARG_TOUR_ID = "tour_id"
        private const val ARG_STATIONS = "stations"

        fun newInstance(tour: Tour): TourDetailsBottomSheet {
            return TourDetailsBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_NAME, tour.name)
                    putString(ARG_DESC, tour.description)
                    putString(ARG_IMAGE, tour.imageUrl)
                    putString(ARG_TOUR_ID, tour.id)

                    val stationNames = ArrayList(
                        tour.stations.map { it.name }.filter { it.isNotBlank() }
                    )
                    putStringArrayList(ARG_STATIONS, stationNames)
                }
            }
        }
    }
}