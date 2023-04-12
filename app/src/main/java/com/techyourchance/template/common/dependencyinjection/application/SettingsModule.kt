package com.techyourchance.template.common.dependencyinjection.application

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.techyourchance.template.common.Constants
import com.techyourchance.template.common.settings.SettingsManager
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
    ): SettingsManager {
        val settingsFactory = SharedPrefsSettingEntriesFactory(sharedPrefs)
        return SettingsManager(settingsFactory)
    }

}