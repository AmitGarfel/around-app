package com.example.around.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class LikesRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    fun checkIfLiked(tourId: String, onResult: (Boolean) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(false)

        db.collection("Tours").document(tourId)
            .collection("likes").document(uid)
            .get()
            .addOnSuccessListener { snap -> onResult(snap.exists()) }
            .addOnFailureListener { onResult(false) }
    }

    fun toggleLike(
        tourId: String,
        onDone: (isLikedNow: Boolean, newLikesCount: Int?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val uid = auth.currentUser?.uid
            ?: return onError(IllegalStateException("User not logged in"))

        val tourRef = db.collection("Tours").document(tourId)
        val likeRef = tourRef.collection("likes").document(uid)

        db.runTransaction { tx ->
            val likeSnap = tx.get(likeRef)
            val tourSnap = tx.get(tourRef)

            val currentCount = (tourSnap.getLong("likesCount") ?: 0L).toInt()

            if (likeSnap.exists()) {
                // UNLIKE
                tx.delete(likeRef)
                tx.update(tourRef, "likesCount", FieldValue.increment(-1))
                Pair(false, (currentCount - 1).coerceAtLeast(0))
            } else {
                // LIKE
                tx.set(likeRef, mapOf("createdAt" to FieldValue.serverTimestamp()))
                tx.update(tourRef, "likesCount", FieldValue.increment(1))
                Pair(true, currentCount + 1)
            }
        }.addOnSuccessListener { (isLikedNow, newCount) ->
            onDone(isLikedNow, newCount)
        }.addOnFailureListener { e ->
            onError(e)
        }
    }
}
