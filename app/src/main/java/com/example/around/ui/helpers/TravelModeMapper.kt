package com.example.around.ui.helpers

import com.example.around.ui.models.TravelModeUi

object TravelModeMapper {

    fun fromSpinnerPosition(position: Int): TravelModeUi =
        when (position) {
            0 -> TravelModeUi("driving", "d")
            1 -> TravelModeUi("walking", "w")
            2 -> TravelModeUi("bicycling", "b")
            3 -> TravelModeUi("transit", "r")
            else -> DEFAULT
        }

    private val DEFAULT = TravelModeUi("driving", "d")
}