package com.techyourchance.android.common.settings

import com.techyourchance.settingshelper.SettingEntriesFactory
import com.techyourchance.settingshelper.SettingEntry

class SettingsManager(
    private val settingsEntriesFactory: SettingEntriesFactory
) {

    fun exampleBooleanSetting(): SettingEntry<Boolean> {
        return settingsEntriesFactory.getSettingEntry(Boolean::class.javaObjectType, KEY_EXAMPLE, false)
    }

    fun authToken(): SettingEntry<String> {
        return settingsEntriesFactory.getSettingEntry(String::class.javaObjectType, KEY_AUTH_TOKEN, "")
    }

    companion object {
        private const val KEY_EXAMPLE = "KEY_EXAMPLE"
        private const val KEY_AUTH_TOKEN = "KEY_AUTH_TOKEN"

    }
}