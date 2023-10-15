package com.techyourchance.android.networking

import com.google.gson.annotations.SerializedName

data class LatestApkInfoResponseSchema(
    @SerializedName("versionName") val versionName: String,
    @SerializedName("versionCode") val versionCode: Int,
    @SerializedName("fileName") val fileName: String,
)
