package com.example.around.data.repositories

import com.google.firebase.firestore.FirebaseFirestore

class UsersRepository(
    private val db: FirebaseFirestore
) {
    fun isAdmin(uid: String, onResult: (Boolean) -> Unit) {
        db.collection("Users").document(uid).get()
            .addOnSuccessListener { doc ->
                onResult(doc.exists() && doc.getBoolean("isAdmin") == true)
            }
            .addOnFailureListener { onResult(false) }
    }

    fun createUser(
        uid: String,
        email: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val userData = hashMapOf(
            "email" to email,
            "isAdmin" to false
        )

        db.collection("Users").document(uid)
            .set(userData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }
}