package com.example.around.domain.usecases

import android.net.Uri
import com.example.around.data.repositories.StorageRepository
import com.example.around.data.repositories.ToursRepository
import com.example.around.domain.model.Tour

class CreateTourUseCase(
    private val toursRepo: ToursRepository,
    private val storageRepo: StorageRepository
) {
    fun createTour(
        tour: Tour,
        imageUri: Uri?,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (imageUri == null) {
            toursRepo.addTour(
                tour,
                onSuccess = onSuccess,
                onError = onError
            )
            return
        }

        storageRepo.uploadTourImage(
            imageUri = imageUri,
            onSuccess = { url ->
                val tourWithImage = tour.copy(imageUrl = url)
                toursRepo.addTour(
                    tourWithImage,
                    onSuccess = onSuccess,
                    onError = onError
                )
            },
            onError = onError
        )
    }
}