package com.example.around.domain.usecases

import com.example.around.data.repositories.AuthRepository
import com.example.around.data.repositories.UsersRepository

class AuthUseCase(
    private val authRepo: AuthRepository,
    private val usersRepo: UsersRepository
) {
    fun register(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        authRepo.register(email, password,
            onSuccess = { uid ->
                // אחרי הרשמה, שמירת פרופיל משתמש ב-Firestore
                usersRepo.createUser(
                    uid = uid,
                    email = email,
                    onSuccess = onSuccess,
                    onError = { e ->
                        // גם אם שמירה נכשלה, עדיין אפשר להמשיך לאפליקציה
                        onSuccess()
                    }
                )
            },
            onError = onError
        )
    }

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        authRepo.login(email, password, onSuccess, onError)
    }

    fun isLoggedIn(): Boolean = authRepo.currentUserId() != null
}
