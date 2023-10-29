package com.techyourchance.android.common.dependencyinjection.application

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.techyourchance.android.common.Constants
import com.techyourchance.android.database.MyDatabase
import com.techyourchance.android.database.entities.backgroundtasksmemory.BackgroundTasksMemoryDao
import com.techyourchance.android.settings.SettingsManager
import com.techyourchance.settingshelper.sharedpreferences.SharedPrefsSettingEntriesFactory
import dagger.Module
import dagger.Provides

@Module
class DatabaseModule {

    @Provides
    @ApplicationScope
    fun myDatabase(application: Application, gson: Gson): MyDatabase {
        return MyDatabase(application, gson)
    }

    @Provides
    @ApplicationScope
    fun backgroundTasksMemoryDao(myDatabase: MyDatabase): BackgroundTasksMemoryDao {
        return myDatabase.backgroundTasksMemoryDao
    }

}