package com.lipata.forkauthority.di

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides

@Module
open class FirebaseModule {
    @Provides
    @ApplicationScope
    open fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

}