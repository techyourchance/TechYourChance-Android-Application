package com.techyourchance.android.networking

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface TechYourChanceApi {

    @GET("/apks/{fileName}")
    suspend fun latestApkInfo(@Path("fileName") fileName: String): Response<LatestApkInfoResponseSchema>

    @GET("/apks/{fileName}")
    suspend fun downloadFile(@Path("fileName") fileName: String): Response<ResponseBody>
}