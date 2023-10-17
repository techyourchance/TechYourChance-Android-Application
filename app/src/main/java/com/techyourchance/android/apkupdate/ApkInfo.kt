package com.techyourchance.android.apkupdate

import java.io.Serializable

data class ApkInfo(
    val isNewerVersion: Boolean,
    val versionName: String,
    val versionCode: Int,
    val apkName: String,
): Serializable {

    companion object {
        val NULL_APK_INFO = ApkInfo(false, "", 0, "")
    }
}
