package com.techyourchance.android.common.dependencyinjection.service

import com.techyourchance.android.backgroundwork.foregroundservice.ForegroundService
import com.techyourchance.android.overlay.ComposeOverlayService
import dagger.Subcomponent

@Subcomponent(modules = [ServiceModule::class])
interface ServiceComponent {
    fun inject(service: ForegroundService)
    fun inject(service: ComposeOverlayService)
}