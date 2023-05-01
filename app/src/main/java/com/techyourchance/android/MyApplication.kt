package com.techyourchance.android

import android.app.Application
import com.techyourchance.android.common.dependencyinjection.application.ApplicationComponent
import com.techyourchance.android.common.dependencyinjection.application.ApplicationModule
import com.techyourchance.android.common.dependencyinjection.application.DaggerApplicationComponent
import com.techyourchance.android.common.dependencyinjection.application.SettingsModule
import com.techyourchance.android.common.logs.TimberDebugTree
import com.techyourchance.android.common.logs.TimberReleaseTree
import timber.log.Timber
import javax.inject.Inject

class MyApplication: Application() {

    @Inject lateinit var timberDebugTree: TimberDebugTree
    @Inject lateinit var timberReleaseTree: TimberReleaseTree

    val applicationComponent: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .settingsModule(SettingsModule())
            .build()
    }

    override fun onCreate() {
        applicationComponent.inject(this)

        super.onCreate()

        initLogs()
    }

    private fun initLogs() {
        if (BuildConfig.DEBUG) {
            Timber.plant(timberDebugTree)
        } else {
            Timber.plant(timberReleaseTree)
        }
    }

}