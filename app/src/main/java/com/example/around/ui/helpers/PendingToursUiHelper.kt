package com.example.around.ui.helpers

import com.example.around.domain.model.Tour

object PendingToursUiHelper {

    fun findPositionByTourId(list: List<Tour>, tourId: String): Int {
        return list.indexOfFirst { it.id == tourId }
    }

    fun removeAt(list: MutableList<Tour>, position: Int): Boolean {
        if (position !in list.indices) return false
        list.removeAt(position)
        return true
    }
}