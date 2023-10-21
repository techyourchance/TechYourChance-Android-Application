package com.techyourchance.android.backgroundwork

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.techyourchance.android.R
import com.techyourchance.android.common.logs.MyLogger
import com.techyourchance.android.common.service.BaseService
import com.techyourchance.android.screens.common.ScreenSpec
import com.techyourchance.android.screens.MainActivity
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.Serializable
import javax.inject.Inject


class ForegroundService: BaseService() {

    @Inject lateinit var foregroundServiceStateManager: ForegroundServiceStateManager
    @Inject lateinit var notificationManager: NotificationManager

    private var isStarted = false

    override fun onCreate() {
        MyLogger.i("onCreate()")
        serviceComponent.inject(this)
        super.onCreate()
    }

    override fun onDestroy() {
        MyLogger.i("onDestroy()")
        super.onDestroy()
        isStarted = false
        coroutineScope.cancel()
        while (coroutineScope.isActive) {
            // busy wait until full cancellation to avoid race condition
        }
        foregroundServiceStateManager.setState(ForegroundServiceState.Stopped)
    }

    override fun onBind(intent: Intent?): IBinder? {
        throw UnsupportedOperationException("binding not supported")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MyLogger.i("onStartCommand()")

        val notificationScreenSpec = intent!!.getSerializableExtra(ScreenSpec.INTENT_EXTRA_SCREEN_SPEC) as ScreenSpec

        if (!isStarted) {
            makeForeground(notificationScreenSpec)
            isStarted = true
            monitorElapsedTime()
        }

        return START_REDELIVER_INTENT
    }

    private fun makeForeground(notificationScreenSpec: ScreenSpec) {
        MyLogger.i("making this service foreground")

        val intent = Intent(this, MainActivity::class.java).also {
            it.putExtra(ScreenSpec.INTENT_EXTRA_SCREEN_SPEC, notificationScreenSpec)
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        createServiceNotificationChannel()

        val notification: Notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.foreground_service_title))
            .setContentText(getString(R.string.foreground_service_text))
            .setSmallIcon(R.drawable.ic_tyc_logo)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(ONGOING_NOTIFICATION_ID, notification)
    }

    private fun createServiceNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.foreground_service_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        notificationManager.createNotificationChannel(channel)
    }

    private fun monitorElapsedTime() {
        coroutineScope.launch {
            var secondsElapsed = 0
            while (isActive) {
                foregroundServiceStateManager.setState(ForegroundServiceState.Started(secondsElapsed))
                delay(1000)
                secondsElapsed++
            }
        }
    }

    companion object {
        private const val ONGOING_NOTIFICATION_ID = 101
        private const val CHANNEL_ID = "1001"

        fun startService(context: Context, notificationScreenSpec: ScreenSpec) {
            val intent = Intent(context, ForegroundService::class.java)
            intent.putExtra(ScreenSpec.INTENT_EXTRA_SCREEN_SPEC, notificationScreenSpec as Serializable)
            context.startForegroundService(intent)
        }

        fun stopService(context: Context) {
            val intent = Intent(context, ForegroundService::class.java)
            context.stopService(intent)
        }

    }
}