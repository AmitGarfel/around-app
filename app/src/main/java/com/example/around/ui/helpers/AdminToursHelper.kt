package com.example.around.ui.helpers

import com.example.around.domain.model.Tour
import com.example.around.domain.model.TourStatus

object AdminToursHelper {

    fun toStatus(isApproved: Boolean): String {
        return if (isApproved) TourStatus.APPROVED else TourStatus.REJECTED
    }

    fun findPositionByTourId(list: List<Tour>, tourId: String): Int {
        return list.indexOfFirst { it.id == tourId }
    }

    fun removeAt(list: MutableList<Tour>, position: Int): Boolean {
        if (position !in list.indices) return false
        list.removeAt(position)
        return true
    }
}