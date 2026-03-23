package com.example.around.ui.helpers

import com.example.around.R

object CreateTourStationFieldsHelper {

    fun stationFieldIds(): List<Int> {
        return listOf(
            R.id.etStation1,
            R.id.etStation2,
            R.id.etStation3,
            R.id.etStation4
        )
    }

    fun stationFieldIdAt(index: Int): Int? {
        return stationFieldIds().getOrNull(index)
    }
}