package com.techyourchance.android.common.device

import android.os.Build
import javax.inject.Inject

class DeviceOsInfoProvider @Inject constructor() {

    fun isAtLeastApi33(): Boolean {
        return Build.VERSION.SDK_INT >= 33
    }

    fun isAtLeastApi31(): Boolean {
        return Build.VERSION.SDK_INT >= 31
    }
}