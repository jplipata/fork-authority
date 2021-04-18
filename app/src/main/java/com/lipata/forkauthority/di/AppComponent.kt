package com.lipata.forkauthority.di

import com.lipata.forkauthority.businesslist.BusinessListActivity
import dagger.Component

@ApplicationScope
@Component(modules = [AppModule::class, YelpModule::class])
interface AppComponent {
    fun inject(target: BusinessListActivity)
}