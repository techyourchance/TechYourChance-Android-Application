package com.techyourchance.android.apkupdate

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.techyourchance.android.BuildConfig
import com.techyourchance.android.common.coroutines.BackgroundDispatcher.Background
import com.techyourchance.android.common.logs.MyLogger
import com.techyourchance.android.common.usecases.UseCaseResult
import com.techyourchance.android.networking.TechYourChanceApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class UpdateApkUseCase @Inject constructor(
    private val techYourChanceApi: TechYourChanceApi,
    private val context: Context,
) {

    suspend fun updateApk(apkInfo: ApkInfo): UseCaseResult<Unit> {
        MyLogger.i("updateApk(); apkInfo: $apkInfo")
        return withContext(Dispatchers.Background) {
            val apkFile = try {
                downloadApk(apkInfo)
            } catch (t: Throwable) {
                MyLogger.e("failed to download the APK", t)
                return@withContext UseCaseResult.Failure(1, t.message ?: "failed to download the APK")
            }

            installApk(apkFile)

            UseCaseResult.Success(Unit)
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun downloadApk(apkInfo: ApkInfo): File {
        MyLogger.i("downloadApk(); apkInfo: $apkInfo")
        val response = techYourChanceApi.downloadFile(apkInfo.apkName)
        if (!response.isSuccessful) {
            throw RuntimeException("apk download failed with error code ${response.code()}")
        }
        val inputStream = response.body()?.byteStream()
        val file = File(context.filesDir, apkInfo.apkName)
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        outputStream.close()
        inputStream?.close()
        return file
    }

    private suspend fun installApk(file: File) {
        withContext(Dispatchers.Unconfined) { // for cancellation check
            MyLogger.i("installApk(); file: $file")
            val contentUri = FileProvider.getUriForFile(
                context,
                "${BuildConfig.APPLICATION_ID}.provider",
                file
            )

            MyLogger.i("sending install intent for URI: $contentUri")

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(contentUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(installIntent)
        }
    }
}