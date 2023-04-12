package com.techyourchance.template

import android.app.Application
import com.techyourchance.template.common.dependencyinjection.application.ApplicationComponent
import com.techyourchance.template.common.dependencyinjection.application.ApplicationModule
import com.techyourchance.template.common.dependencyinjection.application.DaggerApplicationComponent
import com.techyourchance.template.common.dependencyinjection.application.SettingsModule
import com.techyourchance.template.common.logs.TimberDebugTree
import com.techyourchance.template.common.logs.TimberReleaseTree
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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