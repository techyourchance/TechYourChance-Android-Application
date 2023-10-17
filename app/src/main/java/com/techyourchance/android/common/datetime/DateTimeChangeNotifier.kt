package com.techyourchance.android.common.datetime

import androidx.annotation.UiThread
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.Date

interface DateTimeChangeNotifier {

    interface Listener {
        /**
         * will be called once a second with the current LocalDateTime
         */
        @UiThread
        fun onLocalDateTimeChanged(currentLocalDateTime: LocalDateTime)
    }

}