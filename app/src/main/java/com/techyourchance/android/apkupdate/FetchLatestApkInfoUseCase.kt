package com.techyourchance.android.apkupdate

import com.techyourchance.android.common.Constants
import com.techyourchance.android.common.application.AppVersionInfo
import com.techyourchance.android.common.application.AppVersionInfoProvider
import com.techyourchance.android.common.coroutines.BackgroundDispatcher.Background
import com.techyourchance.android.common.datetime.DateTimeProvider
import com.techyourchance.android.common.logs.MyLogger
import com.techyourchance.android.common.usecases.UseCaseResult
import com.techyourchance.android.networking.TechYourChanceApi
import com.techyourchance.android.common.settings.SettingsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FetchLatestApkInfoUseCase @Inject constructor(
    private val techYourChanceApi: TechYourChanceApi,
    private val appVersionInfoProvider: AppVersionInfoProvider,
    private val settingsManager: SettingsManager,
    private val dateTimeProvider: DateTimeProvider,
) {

    suspend fun fetchLatestApkInfo(): UseCaseResult<ApkInfo> {
        MyLogger.i("fetchLatestApkInfo()")
        return withContext(Dispatchers.Background) {
            if (tooEarlyForNewVersionCheck()) {
                return@withContext UseCaseResult.Success(ApkInfo.NULL_APK_INFO)
            }
            val appVersionInfo = appVersionInfoProvider.getAppVersionInfo()
            val latestApkInfoResponse = try {
                 techYourChanceApi.latestApkInfo(getLatestApkInfoFileName(appVersionInfo))
            } catch (e: Throwable) {
                MyLogger.e("failed to fetch latest APK info", e)
                return@withContext UseCaseResult.Failure(1, "failed to fetch latest APK info")
            }
            val responseSchema = latestApkInfoResponse.body()
            return@withContext if (responseSchema != null) {
                settingsManager.lastApkVersionCheckTimestamp().value = dateTimeProvider.getTimestampUtc()
                val isNewerVersion = responseSchema.versionCode > appVersionInfo.versionCode
                UseCaseResult.Success(
                    ApkInfo(
                        isNewerVersion = isNewerVersion,
                        versionName = responseSchema.versionName,
                        versionCode = responseSchema.versionCode,
                        apkName = responseSchema.fileName
                    )
                )
            } else {
                UseCaseResult.Failure(latestApkInfoResponse.code(), "failed to fetch latest APK info")
            }
        }
    }

    private fun getLatestApkInfoFileName(currentAppVersionInfo: AppVersionInfo): String {
        return if (currentAppVersionInfo.buildType.lowercase().contains("release")) {
            Constants.LATEST_APK_INFO_FILE_NAME_RELEASE
        } else {
            Constants.LATEST_APK_INFO_FILE_NAME_DEBUG
        }
    }

    private fun tooEarlyForNewVersionCheck(): Boolean {
        return dateTimeProvider.getTimestampUtc() <= settingsManager.lastApkVersionCheckTimestamp().value + Constants.APK_VERSION_CHECK_MIN_INTERVAL_MS
    }
}