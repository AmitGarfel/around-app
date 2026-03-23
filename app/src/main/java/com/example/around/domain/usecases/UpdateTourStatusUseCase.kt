package com.example.around.domain.usecases

import com.example.around.data.repositories.ToursRepository

class UpdateTourStatusUseCase(
    private val toursRepository: ToursRepository
) {
    operator fun invoke(
        tourId: String,
        newStatus: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        toursRepository.updateStatus(
            tourId = tourId,
            newStatus = newStatus,
            onSuccess = onSuccess,
            onError = { onError() }
        )
    }
}