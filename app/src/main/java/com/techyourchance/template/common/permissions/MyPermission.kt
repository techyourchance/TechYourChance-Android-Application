package com.techyourchance.template.common.permissions

import android.Manifest
import java.lang.RuntimeException

enum class MyPermission(val androidPermission: String) {

    CAMERA(Manifest.permission.CAMERA);

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