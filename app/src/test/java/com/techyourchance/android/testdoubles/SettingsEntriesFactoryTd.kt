package com.techyourchance.android.testdoubles

import com.techyourchance.settingshelper.SettingEntriesFactory
import com.techyourchance.settingshelper.SettingEntry

class SettingsEntriesFactoryTd : SettingEntriesFactory() {

    override fun <T : Any?> newSettingEntry(clazz: Class<T>?, key: String?, defaultValue: T): SettingEntry<T> {
        return object: SettingEntry<T>(key, defaultValue) {
            private var inMemoryValue: T? = null

            override fun getValue(): T {
                return inMemoryValue ?: defaultValue
            }

            override fun setValue(value: T) {
                inMemoryValue = value
            }

            override fun remove() {
                inMemoryValue = null
            }
        }
    }
}