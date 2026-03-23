package com.example.around.domain.usecases

import com.example.around.data.repositories.ToursRepository
import com.example.around.domain.model.TourStationsData
import com.example.around.util.CityNormalizer

class LoadTourStationsUseCase(
    private val toursRepo: ToursRepository
) {

    fun load(
        tourId: String,
        onSuccess: (TourStationsData) -> Unit,
        onError: (Exception) -> Unit
    ) {
        toursRepo.getTourById(
            tourId = tourId,
            onSuccess = { tour ->
                onSuccess(
                    TourStationsData(
                        tourName = tour.name,
                        city = CityNormalizer.canonical(tour.city),
                        stations = tour.stations
                    )
                )
            },
            onError = onError
        )
    }
}