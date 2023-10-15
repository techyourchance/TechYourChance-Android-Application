package com.techyourchance.android.common.application

import com.techyourchance.android.BuildConfig
import javax.inject.Inject

class AppVersionInfoProvider @Inject constructor() {

    fun getAppVersionInfo(): AppVersionInfo {
        return AppVersionInfo(
            BuildConfig.BUILD_TYPE,
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE,
        )
    }
}