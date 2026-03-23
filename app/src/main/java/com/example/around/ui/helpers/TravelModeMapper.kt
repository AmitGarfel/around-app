package com.example.around.ui.helpers

import com.example.around.ui.models.TravelModeUi

object TravelModeMapper {

    fun fromSpinnerPosition(position: Int): TravelModeUi {
        return when (position) {
            0 -> TravelModeUi(
                dirMode = "driving",
                navMode = "d"
            )

            1 -> TravelModeUi(
                dirMode = "walking",
                navMode = "w"
            )

            2 -> TravelModeUi(
                dirMode = "bicycling",
                navMode = "b"
            )

            3 -> TravelModeUi(
                dirMode = "transit",
                navMode = "r"
            )

            else -> TravelModeUi(
                dirMode = "driving",
                navMode = "d"
            )
        }
    }
}