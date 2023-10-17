package com.techyourchance.android.testdoubles

import com.techyourchance.android.common.datetime.DateTimeProvider
import java.time.*
import java.util.*

class DateTimeProviderImplTd : DateTimeProvider {

    var stubZonedDateTime: ZonedDateTime? = null

    override fun getLocalDate(): LocalDate {
        return getZonedDateTimeUtc().toLocalDate()
    }

    override fun getLocalTime(): LocalTime {
        return getZonedDateTimeUtc().toLocalTime()
    }

    override fun getLocalDateTime(): LocalDateTime {
        return getZonedDateTimeUtc().toLocalDateTime()
    }

    override fun getDateUtc(): Date {
        return Date(getTimestampUtc())
    }

    override fun getTimestampUtc(): Long {
        return getZonedDateTimeUtc().toInstant().toEpochMilli()
    }

    override fun getZonedDateTimeUtc(): ZonedDateTime {
        return stubZonedDateTime?.withZoneSameInstant(ZoneOffset.UTC) ?: ZonedDateTime.now(ZoneOffset.UTC)
    }
}