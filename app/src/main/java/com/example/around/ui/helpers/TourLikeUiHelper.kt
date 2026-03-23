package com.example.around.ui.helpers

import com.example.around.domain.model.Tour

object TourLikeUiHelper {

    data class PreviousState(
        val wasLiked: Boolean,
        val likesCount: Int
    )

    fun applyOptimisticToggle(tour: Tour): PreviousState {
        val previous = PreviousState(
            wasLiked = tour.isLikedByMe,
            likesCount = tour.likesCount
        )

        tour.isLikedByMe = !tour.isLikedByMe
        tour.likesCount = if (tour.isLikedByMe) {
            previous.likesCount + 1
        } else {
            (previous.likesCount - 1).coerceAtLeast(0)
        }

        return previous
    }

    fun restorePreviousState(tour: Tour, previous: PreviousState) {
        tour.isLikedByMe = previous.wasLiked
        tour.likesCount = previous.likesCount
    }
}