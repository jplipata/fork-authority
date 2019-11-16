package com.lipata.forkauthority.di

import com.google.firebase.firestore.FirebaseFirestore
import com.lipata.forkauthority.data.user.UserIdentityManager
import dagger.Component

@ApplicationScope
@Component(modules = [TestFirebaseModule::class, TestAppModule::class, YelpModule::class])
interface TestAppComponent : AppComponent {
    fun firestore(): FirebaseFirestore
    fun userIdentityManager(): UserIdentityManager
}