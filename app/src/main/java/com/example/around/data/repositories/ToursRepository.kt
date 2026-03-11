package com.example.around.data.repositories

import com.example.around.data.mappers.toTourSafe
import com.example.around.domain.model.Station
import com.example.around.domain.model.Tour
import com.google.firebase.firestore.FirebaseFirestore

class ToursRepository(
    private val db: FirebaseFirestore
) {
    fun getPendingTours(
        onSuccess: (List<Tour>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection("Tours")
            .whereEqualTo("status", "pending")
            .get()
            .addOnSuccessListener { docs ->
                onSuccess(docs.map { it.toTourSafe() })
            }
            .addOnFailureListener { onError(it) }
    }

    fun updateStatus(
        tourId: String,
        newStatus: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection("Tours").document(tourId)
            .update("status", newStatus)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    fun getApprovedToursByMoodAndTime(
        mood: String,
        time: String,
        onSuccess: (List<Tour>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection("Tours")
            .whereEqualTo("mood", mood)
            .whereEqualTo("timeTag", time)
            .whereEqualTo("status", "approved")
            .get()
            .addOnSuccessListener { docs ->
                onSuccess(docs.map { it.toTourSafe() })
            }
            .addOnFailureListener { onError(it) }
    }

    fun addTour(
        tour: Tour,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection("Tours")
            .add(tour)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    fun getTourById(
        tourId: String,
        onSuccess: (Tour) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection("Tours").document(tourId)
            .get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    onError(IllegalStateException("Tour not found"))
                    return@addOnSuccessListener
                }
                onSuccess(doc.toTourSafe())
            }
            .addOnFailureListener { onError(it) }
    }

    fun updateStations(
        tourId: String,
        updatedStations: List<Station>,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection("Tours").document(tourId)
            .update("stations", updatedStations)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }
}