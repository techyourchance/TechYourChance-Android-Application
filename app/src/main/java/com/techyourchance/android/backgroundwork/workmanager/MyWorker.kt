package com.techyourchance.android.backgroundwork.workmanager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.ForegroundInfo
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.techyourchance.android.R
import com.techyourchance.android.common.logs.MyLogger
import com.techyourchance.android.screens.common.ScreenSpec
import com.techyourchance.android.screens.MainActivity

class MyWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val myWorkerManager: MyWorkerManager,
    private val notificationManager: NotificationManager,
): Worker(context, workerParams) {

    private var workerThread: Thread? = null

    override fun doWork(): Result {
        val maxRetries = inputData.getInt(DATA_KEY_MAX_RETRIES, 1)
        val currentAttempt = runAttemptCount + 1
        MyLogger.i("doWork(); attempt: $currentAttempt/$maxRetries")

        workerThread = Thread.currentThread()

        try {
            Thread.sleep(2000L) // simulate some work
        } catch (e: InterruptedException) {
            if (isStopped) {
                return Result.failure()
            } else {
                throw RuntimeException("unexpected interrupt")
            }
        }

        MyLogger.i("doWork(); attempt completed: $currentAttempt/$maxRetries")

        return if (currentAttempt < maxRetries) {
            MyLogger.i("doWork(); retry the work")
            Result.retry()
        } else {
            MyLogger.i("doWork(); work succeeded")
            myWorkerManager.setState(MyWorkerState.Succeeded)
            Result.success()
        }
    }

    override fun getForegroundInfo(): ForegroundInfo {

        val intent = Intent(applicationContext, MainActivity::class.java).also {
            it.putExtra(ScreenSpec.INTENT_EXTRA_SCREEN_SPEC, ScreenSpec.WorkManager)
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        createNotificationChannel()

        val notification: Notification = Notification.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(applicationContext.getString(R.string.work_manager_notification_title))
            .setContentText(applicationContext.getString(R.string.work_manager_notification_text))
            .setSmallIcon(R.drawable.ic_tyc_logo)
            .setContentIntent(pendingIntent)
            .build()

        return ForegroundInfo(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            applicationContext.getString(R.string.work_manager_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        notificationManager.createNotificationChannel(channel)
    }

    override fun onStopped() {
        MyLogger.i("onStopped()")
        super.onStopped()
        workerThread?.interrupt()
    }

    companion object {
        const val DATA_KEY_MAX_RETRIES = "DATA_KEY_MAX_RETRIES"

        private const val CHANNEL_ID = "2002"
        private const val NOTIFICATION_ID = 202
    }
}