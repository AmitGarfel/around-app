package com.example.around.di

import android.content.Context
import com.example.around.data.geo.GeocodingRepository
import com.example.around.data.geo.MapsNavigationRepository
import com.example.around.data.repositories.AuthRepository
import com.example.around.data.repositories.LikesRepository
import com.example.around.data.repositories.StorageRepository
import com.example.around.data.repositories.ToursRepository
import com.example.around.data.repositories.UsersRepository
import com.example.around.domain.usecases.AuthUseCase
import com.example.around.domain.usecases.CreateTourUseCase
import com.example.around.domain.usecases.LoadToursWithLikesUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object AppGraph {

    // Firebase singletons
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    // Repositories
    val toursRepo: ToursRepository by lazy { ToursRepository(db) }
    val usersRepo: UsersRepository by lazy { UsersRepository(db) }
    val storageRepo: StorageRepository by lazy { StorageRepository(storage) }
    val likesRepo: LikesRepository by lazy { LikesRepository(db, auth) }
    val authRepo: AuthRepository by lazy { AuthRepository(auth) }

    // UseCases
    val createTourUseCase: CreateTourUseCase by lazy {
        CreateTourUseCase(toursRepo, storageRepo)
    }

    val loadToursWithLikesUseCase: LoadToursWithLikesUseCase by lazy {
        LoadToursWithLikesUseCase(
            toursRepo = toursRepo,
            likesRepo = likesRepo,
            auth = auth
        )
    }

    val authUseCase: AuthUseCase by lazy {
        AuthUseCase(authRepo, usersRepo)
    }

    // Context-based providers
    fun geocodingRepo(context: Context): GeocodingRepository =
        GeocodingRepository(context.applicationContext)

    fun mapsNavRepo(context: Context): MapsNavigationRepository =
        MapsNavigationRepository(context.applicationContext)
}