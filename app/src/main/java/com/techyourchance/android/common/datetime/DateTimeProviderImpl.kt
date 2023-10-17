package com.techyourchance.android.common.datetime

import android.os.Handler
import android.os.Looper
import com.techyourchance.android.common.Observable
import java.time.*
import java.util.*

class DateTimeProviderImpl : Observable<DateTimeChangeNotifier.Listener>(), DateTimeProvider, DateTimeChangeNotifier {

    private val uiHandler = Handler(Looper.getMainLooper())

    private val listenersNotificationRunnable: Runnable = object : Runnable {
        override fun run() {
            listeners.map { it.onLocalDateTimeChanged(getLocalDateTime()) }
            uiHandler.postDelayed(this, 1000)
        }
    }

    override fun getLocalDate(): LocalDate {
        return LocalDate.now()
    }

    override fun getLocalTime(): LocalTime {
        return LocalTime.now()
    }

    override fun getLocalDateTime(): LocalDateTime {
        return LocalDateTime.of(getLocalDate(), getLocalTime())
    }

    override fun getDateUtc(): Date {
        return Date.from(getZonedDateTimeUtc().toInstant())
    }

    override fun getTimestampUtc(): Long {
        return System.currentTimeMillis()
    }

    override fun getZonedDateTimeUtc(): ZonedDateTime {
        return ZonedDateTime.now(ZoneOffset.UTC)
    }

    override fun onFirstListenerRegistered() {
        super.onFirstListenerRegistered()
        uiHandler.post(listenersNotificationRunnable)
    }

    override fun onLastListenerUnregistered() {
        super.onLastListenerUnregistered()
        uiHandler.removeCallbacks(listenersNotificationRunnable)
    }

}