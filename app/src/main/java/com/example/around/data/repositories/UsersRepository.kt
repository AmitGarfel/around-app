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
        firstName: String,
        lastName: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val userData = hashMapOf(
            "email" to email,
            "firstName" to firstName,
            "lastName" to lastName,
            "fullName" to "$firstName $lastName",
            "isAdmin" to false
        )

        db.collection("Users").document(uid)
            .set(userData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    fun getUserFirstName(
        uid: String,
        onResult: (String?) -> Unit
    ) {
        db.collection("Users").document(uid).get()
            .addOnSuccessListener { doc ->
                val firstName = doc.getString("firstName")?.trim()
                onResult(firstName)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun getUserFullName(
        uid: String,
        onResult: (String?) -> Unit
    ) {
        db.collection("Users").document(uid).get()
            .addOnSuccessListener { doc ->
                val firstName = doc.getString("firstName")?.trim().orEmpty()
                val lastName = doc.getString("lastName")?.trim().orEmpty()

                val fullName = when {
                    firstName.isNotBlank() && lastName.isNotBlank() -> "$firstName $lastName"
                    firstName.isNotBlank() -> firstName
                    else -> doc.getString("fullName")?.trim()
                }

                onResult(fullName)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun getUserProfile(
        uid: String,
        onResult: (firstName: String?, lastName: String?, email: String?) -> Unit
    ) {
        db.collection("Users").document(uid).get()
            .addOnSuccessListener { doc ->
                val firstName = doc.getString("firstName")?.trim()
                val lastName = doc.getString("lastName")?.trim()
                val email = doc.getString("email")?.trim()
                onResult(firstName, lastName, email)
            }
            .addOnFailureListener {
                onResult(null, null, null)
            }
    }
}