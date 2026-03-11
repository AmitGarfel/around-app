package com.example.around.domain.usecases

import com.example.around.domain.model.Tour
import com.example.around.data.repositories.ToursRepository
import com.example.around.data.repositories.LikesRepository
import com.google.firebase.auth.FirebaseAuth

class LoadToursWithLikesUseCase(
    private val toursRepo: ToursRepository,
    private val likesRepo: LikesRepository,
    private val auth: FirebaseAuth
) {

    fun load(
        mood: String,
        time: String,
        onSuccess: (List<Tour>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val userId = auth.currentUser?.uid

        toursRepo.getApprovedToursByMoodAndTime(
            mood = mood,
            time = time,
            onSuccess = { tours ->

                if (tours.isEmpty() || userId == null) {
                    onSuccess(tours)
                    return@getApprovedToursByMoodAndTime
                }

                var checked = 0
                for (tour in tours) {
                    likesRepo.checkIfLiked(tour.id) { isLiked ->
                        tour.isLikedByMe = isLiked
                        checked++

                        if (checked == tours.size) {
                            onSuccess(tours)
                        }
                    }
                }
            },
            onError = onError
        )
    }
}
