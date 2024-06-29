package com.techyourchance.android.common.dependencyinjection.application

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.techyourchance.android.common.Constants
import com.techyourchance.android.common.settings.SettingsManager
import com.techyourchance.settingshelper.sharedpreferences.SharedPrefsSettingEntriesFactory
import dagger.Module
import dagger.Provides

@Module
class SettingsModule {

    @Provides
    fun sharedPrefs(application: Application): SharedPreferences {
        return application.getSharedPreferences(Constants.SHARED_PREFS_FILE, Context.MODE_PRIVATE)
    }

    @Provides
    @ApplicationScope
    fun settingsManager(
        sharedPrefs: SharedPreferences,
        gson: Gson
    ): SettingsManager {
        val settingsFactory = SharedPrefsSettingEntriesFactory(sharedPrefs)
        return SettingsManager(settingsFactory, gson)
    }

}