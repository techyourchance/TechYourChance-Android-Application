package com.techyourchance.android.common.dependencyinjection.service

import com.techyourchance.android.backgroundwork.ForegroundService
import dagger.Subcomponent

@Subcomponent(modules = [ServiceModule::class])
interface ServiceComponent {
    fun inject(service: ForegroundService)
}