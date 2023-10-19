package com.techyourchance.android

import android.app.Application
import androidx.work.Configuration
import com.techyourchance.android.backgroundwork.workmanager.MyWorkerFactory
import com.techyourchance.android.common.dependencyinjection.application.ApplicationComponent
import com.techyourchance.android.common.dependencyinjection.application.ApplicationModule
import com.techyourchance.android.common.dependencyinjection.application.DaggerApplicationComponent
import com.techyourchance.android.common.dependencyinjection.application.SettingsModule
import com.techyourchance.android.common.logs.TimberDebugTree
import com.techyourchance.android.common.logs.TimberReleaseTree
import io.sentry.android.core.SentryAndroid
import timber.log.Timber
import javax.inject.Inject


class MyApplication: Application(), Configuration.Provider {

    @Inject lateinit var timberDebugTree: TimberDebugTree
    @Inject lateinit var timberReleaseTree: TimberReleaseTree
    @Inject lateinit var myWorkerFactory: MyWorkerFactory

    val applicationComponent: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .settingsModule(SettingsModule())
            .build()
    }

    override fun onCreate() {
        applicationComponent.inject(this)

        SentryAndroid.init(this) { options ->
            options.dsn = BuildConfig.SENTRY_DSN
        }

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

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .setWorkerFactory(myWorkerFactory)
            .build()
    }
}