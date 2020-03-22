package com.lipata.forkauthority.di

import com.google.firebase.firestore.FirebaseFirestore
import com.nhaarman.mockitokotlin2.mock
import dagger.Module
import dagger.Provides

@Module
class TestFirebaseModule {
    @Provides
    @ApplicationScope
    fun provideFirestore(): FirebaseFirestore {
        return mock()
    }
}