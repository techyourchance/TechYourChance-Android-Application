package com.techyourchance.android.backgroundwork

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import androidx.annotation.WorkerThread
import com.techyourchance.android.R
import com.techyourchance.android.common.eventbus.EventBusPoster
import com.techyourchance.android.common.logs.MyLogger
import com.techyourchance.android.common.service.BaseService
import com.techyourchance.android.screens.main.MainActivity
import kotlinx.coroutines.*
import java.nio.Buffer
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject
import kotlin.concurrent.withLock


class ForegroundService: BaseService() {

    @Inject lateinit var foregroundServiceStateManager: ForegroundServiceStateManager

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

        if (intent == null) {
            throw IllegalStateException("this service mustn't receive null intents")
        }

        if (!isStarted) {
            makeForeground()
            isStarted = true
            monitorElapsedTime()
        }

        return START_NOT_STICKY
    }

    private fun makeForeground() {
        MyLogger.i("making this service foreground")

        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
            }

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
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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
        private const val CHANNEL_NAME = "Screen capture channel"


        fun startService(context: Context) {
            val intent = Intent(context, ForegroundService::class.java)
            context.startForegroundService(intent)
        }

        fun stopService(context: Context) {
            val intent = Intent(context, ForegroundService::class.java)
            context.stopService(intent)
        }

    }
}