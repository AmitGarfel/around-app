package com.example.around.data.repositories

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class StorageRepository(
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {
    fun uploadTourImage(
        imageUri: Uri,
        onSuccess: (downloadUrl: String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val ref = storage.reference.child("tours/${UUID.randomUUID()}.jpg")

        ref.putFile(imageUri)
            .addOnSuccessListener {
                ref.downloadUrl
                    .addOnSuccessListener { url -> onSuccess(url.toString()) }
                    .addOnFailureListener { e -> onError(e) }
            }
            .addOnFailureListener { e -> onError(e) }
    }
}