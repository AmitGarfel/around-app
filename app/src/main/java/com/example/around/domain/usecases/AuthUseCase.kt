package com.example.around.domain.usecases

import com.example.around.data.repositories.AuthRepository
import com.example.around.data.repositories.UsersRepository

class AuthUseCase(
    private val authRepo: AuthRepository,
    private val usersRepo: UsersRepository
) {
    fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        authRepo.register(
            email = email,
            password = password,
            onSuccess = { uid ->
                usersRepo.createUser(
                    uid = uid,
                    email = email,
                    firstName = firstName,
                    lastName = lastName,
                    onSuccess = onSuccess,
                    onError = {
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

    fun currentUserId(): String? = authRepo.currentUserId()

    fun getCurrentUserFirstName(
        onResult: (String?) -> Unit
    ) {
        val uid = authRepo.currentUserId()
        if (uid == null) {
            onResult(null)
            return
        }

        usersRepo.getUserFirstName(uid, onResult)
    }
}