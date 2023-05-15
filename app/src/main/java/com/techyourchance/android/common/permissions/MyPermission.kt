package com.techyourchance.android.common.permissions

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import java.lang.RuntimeException

enum class MyPermission(val androidPermission: String) {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    POST_NOTIFICATIONS(Manifest.permission.POST_NOTIFICATIONS),

    CAMERA(Manifest.permission.CAMERA),

    ;

    companion object {
        @JvmStatic
        fun fromAndroidPermission(androidPermission: String): MyPermission {
            for (permission in values()) {
                if (permission.androidPermission == androidPermission) {
                    return permission
                }
            }
            throw RuntimeException("Android permission not supported yet: $androidPermission")
        }
    }
}