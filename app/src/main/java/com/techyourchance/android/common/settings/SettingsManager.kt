package com.techyourchance.android.common.settings

import com.google.gson.Gson
import com.techyourchance.android.backgroundwork.workmanager.MyWorkerConfig
import com.techyourchance.settingshelper.SettingEntriesFactory
import com.techyourchance.settingshelper.SettingEntry

class SettingsManager(
    private val settingsEntriesFactory: SettingEntriesFactory,
    private val gson: Gson,
) {

    fun exampleBooleanSetting(): SettingEntry<Boolean> {
        return settingsEntriesFactory.getSettingEntry(Boolean::class.javaObjectType, KEY_EXAMPLE, false)
    }

    fun authToken(): SettingEntry<String> {
        return settingsEntriesFactory.getSettingEntry(String::class.javaObjectType, KEY_AUTH_TOKEN, "")
    }

    fun myWorkerConfig(): SettingEntry<MyWorkerConfig> {
        return object: SettingEntry<MyWorkerConfig>(KEY_WORKER_CONFIG, MyWorkerConfig(false, 0, 0)) {

            private val backingEntry = settingsEntriesFactory.getSettingEntry(String::class.javaObjectType, KEY_WORKER_CONFIG, "")

            override fun getValue(): MyWorkerConfig {
                val backingEntryValue = backingEntry.value
                if (backingEntryValue.isBlank()) {
                    return defaultValue
                }
                return gson.fromJson(backingEntryValue, MyWorkerConfig::class.java)
            }

            override fun setValue(value: MyWorkerConfig) {
                backingEntry.value = gson.toJson(value)
            }

            override fun remove() {
                backingEntry.remove()
            }
        }
    }

    companion object {
        private const val KEY_EXAMPLE = "KEY_EXAMPLE"
        private const val KEY_AUTH_TOKEN = "KEY_AUTH_TOKEN"
        private const val KEY_WORKER_CONFIG = "KEY_WORKER_CONFIG"
    }
}