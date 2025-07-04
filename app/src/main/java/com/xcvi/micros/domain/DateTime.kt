package com.xcvi.micros.domain

import android.os.Build
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.atTime
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle
import java.util.Locale


/**
 * Stats
 */
fun generateWeeksBetween(startDate: Int?, endDate: Int?): List<Int> {
    if (startDate == null || endDate == null) return emptyList()
    val weeks = mutableListOf<Int>()
    var current = startDate.getStartOfWeek()
    val end = endDate.getStartOfWeek()
    while (current <= end) {
        weeks.add(current)
        current += 7
    }
    return weeks
}

fun generateMonthsBetween(startDate: Int?, endDate: Int?): List<Int> {
    if (startDate == null || endDate == null) return emptyList()
    val months = mutableListOf<Int>()
    var current = startDate.getStartOfMonth().getLocalDate().month.ordinal + 1
    val lastMonth = endDate.getStartOfMonth().getLocalDate().month.ordinal + 1

    while (current <= lastMonth) {
        months.add(current)
        current += 1
    }
    return months.map { monthOrdinal ->
        LocalDate(
            year = startDate.getLocalDate().year,
            monthNumber = monthOrdinal, dayOfMonth = 1
        ).toEpochDays()
    }
}

/**
 * Get Current
 */
fun getNow(): Long {
    return Clock.System.now().toEpochMilliseconds()
}

fun getToday(): Int {
    val instant = Instant.fromEpochMilliseconds(getNow())
    val timeZone = TimeZone.currentSystemDefault()
    return instant.toLocalDateTime(timeZone).date.toEpochDays()
}

/**
 * Int -> Long
 */
fun Int.getStartTimestamp(): Long {
    val date = LocalDate.fromEpochDays(this)
    return date.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
}

fun Int.getEndTimestamp(): Long {
    val date = LocalDate.fromEpochDays(this)
    return date.atTime(23, 59, 59, 999).toInstant(TimeZone.currentSystemDefault())
        .toEpochMilliseconds()
}

fun Int.getTimestamp(hour: Int, minute: Int, seconds: Int): Long {
    val date = LocalDate.fromEpochDays(this)
    return date.atTime(hour, minute, seconds).toInstant(TimeZone.currentSystemDefault())
        .toEpochMilliseconds()
}

fun Int.getTimestamp(hour: Int, minute: Int): Long {
    val date = LocalDate.fromEpochDays(this)
    return date.atTime(hour, minute, 0, 0).toInstant(TimeZone.currentSystemDefault())
        .toEpochMilliseconds()
}


/**
 * Long -> Int
 */

fun Long.getEpochDate(): Int {
    val instant = Instant.fromEpochMilliseconds(this)
    val timeZone = TimeZone.currentSystemDefault()
    return instant.toLocalDateTime(timeZone).date.toEpochDays()
}


/**
 * Int -> LocalDate
 */

fun Int.getLocalDate(): LocalDate {
    return LocalDate.fromEpochDays(this)
}

fun Int.getStartOfWeek(): Int {
    val localDate = LocalDate.fromEpochDays(this)
    val dayOfWeek = localDate.dayOfWeek.isoDayNumber  // Monday=1, Sunday=7

    val daysToSubtract = (dayOfWeek - 1) // how many days since Monday
    val monday = localDate.minus(DatePeriod(days = daysToSubtract))
    return monday.toEpochDays()
}

fun Int.getEndOfWeek(): Int {
    return this.getStartOfWeek() + 6
}

fun Int.getStartOfMonth(): Int {
    val localDate = LocalDate.fromEpochDays(this)
    return LocalDate(localDate.year, localDate.month.ordinal, 1).toEpochDays()
}

fun Int.getEndOfMonth(): Int {
    val localDate = LocalDate.fromEpochDays(this)
    val nextMonth = LocalDate(localDate.year, localDate.month.ordinal + 1, 1)
    return nextMonth.toEpochDays() - 1
}


/**
 * Long -> DateTime
 */

fun Long.getLocalDate(): LocalDate {
    val instant = Instant.fromEpochMilliseconds(this)
    val timeZone = TimeZone.currentSystemDefault()
    val localDate = instant.toLocalDateTime(timeZone)
    return localDate.date
}

fun Long.getLocalDateTime(): LocalDateTime {
    val instant = Instant.fromEpochMilliseconds(this)
    val timeZone = TimeZone.currentSystemDefault()
    return instant.toLocalDateTime(timeZone)
}


/**
 * Int -> Formatted Date
 */
fun LocalDate.monthFormatted(short: Boolean = false, locale: Locale = Locale.getDefault()): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val style = if (short) TextStyle.SHORT else TextStyle.FULL
        this.month.getDisplayName(style, locale).lowercase()
            .replaceFirstChar { it.uppercase() }

    } else {
        // Fallback: format manually (non-localized)
        val name = this.month.name
        if (short) name.substring(0, 3).lowercase().replaceFirstChar { it.uppercase() }
        else name.lowercase().replaceFirstChar { it.uppercase() }
    }
}

fun LocalDate.dayOfWeekFormatted(
    short: Boolean = false,
    locale: Locale = Locale.getDefault()
): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val style = if (short) TextStyle.SHORT else TextStyle.FULL
        this.dayOfWeek.getDisplayName(style, locale).lowercase().replaceFirstChar { it.uppercase() }
    } else {
        // Fallback: format manually (non-localized)
        val name = this.dayOfWeek.name
        if (short) name.substring(0, 3).lowercase().replaceFirstChar { it.uppercase() }
        else name.lowercase().replaceFirstChar { it.uppercase() }
    }
}

fun Int.formatEpochDate(
    short: Boolean = true, showDayOfWeek: Boolean = false
): String {
    val date = LocalDate.fromEpochDays(this)
    val month = date.monthFormatted(short)
    val dayOfWeek = date.dayOfWeekFormatted(short)

    return if (showDayOfWeek) {
        "$dayOfWeek, $month ${date.dayOfMonth}"
    } else {
        "$month ${date.dayOfMonth}"
    }
}


/**
 * Long -> Formatted Date
 */

fun Long.formatTimestamp(
    short: Boolean = true,
    showDayOfWeek: Boolean = false,
    showTime: Boolean = false
): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val timeZone = TimeZone.currentSystemDefault()
    val localDate = instant.toLocalDateTime(timeZone)

    val dateFormatted =
        localDate.date.toEpochDays().formatEpochDate(short = short, showDayOfWeek = showDayOfWeek)
    val timeFormatted = if (showTime) {
        "${localDate.time.hour}:${localDate.time.minute}"
    } else {
        ""
    }

    return "$dateFormatted $timeFormatted"
}













