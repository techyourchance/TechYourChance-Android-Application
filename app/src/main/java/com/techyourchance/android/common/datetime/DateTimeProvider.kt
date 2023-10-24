package com.techyourchance.android.common.datetime

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.Date

interface DateTimeProvider {
    fun getLocalDate(): LocalDate
    fun getLocalTime(): LocalTime
    fun getLocalDateTime(): LocalDateTime
    fun getDateUtc(): Date
    fun getTimestampUtc(): Long
    fun getZonedDateTimeUtc(): ZonedDateTime
    fun getNanoTime(): Long
}