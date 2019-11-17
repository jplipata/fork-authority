package com.lipata.forkauthority.di

import com.lipata.forkauthority.businesslist.BusinessListActivity
import com.lipata.forkauthority.poll.PollActivity
import com.lipata.forkauthority.poll.ViewPollFragment
import dagger.Component

@ApplicationScope
@Component(modules = [AppModule::class, YelpModule::class, FirebaseModule::class])
interface AppComponent {
    fun inject(target: BusinessListActivity)

    fun inject(activity: PollActivity)

    fun inject(target: ViewPollFragment)
}