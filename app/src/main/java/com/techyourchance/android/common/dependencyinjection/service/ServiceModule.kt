package com.techyourchance.android.common.dependencyinjection.service

import android.app.Service
import android.content.Context
import android.view.WindowManager
import dagger.Module
import dagger.Provides

@Module
class ServiceModule(private val service: Service) {

    @Provides
    fun context(): Context {
        return service
    }

    @Provides
    fun service(): Service {
        return service
    }

    @Provides
    fun windowManager(service: Service): WindowManager {
        return service.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }
}