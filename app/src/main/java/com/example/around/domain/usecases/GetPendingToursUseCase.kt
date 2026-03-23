package com.example.around.domain.usecases

import com.example.around.data.repositories.ToursRepository
import com.example.around.domain.model.Tour

class GetPendingToursUseCase(
    private val toursRepository: ToursRepository
) {
    operator fun invoke(
        onSuccess: (List<Tour>) -> Unit,
        onError: () -> Unit
    ) {
        toursRepository.getPendingTours(
            onSuccess = onSuccess,
            onError = { onError() }
        )
    }
}