package com.xcvi.micros.domain

import android.os.Build
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle
import java.util.Locale


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
    return date.atTime(23, 59, 59, 999).toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
}
fun Int.getTimestamp(hour: Int, minute: Int, seconds: Int): Long {
    val date = LocalDate.fromEpochDays(this)
    return date.atTime(hour, minute, seconds).toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
}

fun Int.getTimestamp(hour: Int, minute: Int): Long {
    val date = LocalDate.fromEpochDays(this)
    return date.atTime(hour, minute, 0,0).toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
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

/**
 * Long -> DateTime
 */

fun Long.getLocalDate(): LocalDate {
    val instant = Instant.fromEpochMilliseconds(this)
    val timeZone = TimeZone.currentSystemDefault()
    val localDate =  instant.toLocalDateTime(timeZone)
    return localDate.date
}
fun Long.getLocalDateTime(short: Boolean = true): LocalDateTime {
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
        this.month.getDisplayName(style, locale).substring(0, 3).lowercase().replaceFirstChar { it.uppercase() }
    } else {
        // Fallback: format manually (non-localized)
        val name = this.month.name
        if (short) name.substring(0, 3).lowercase().replaceFirstChar { it.uppercase() }
        else name.lowercase().replaceFirstChar { it.uppercase() }
    }
}
fun LocalDate.dayOfWeekFormatted(short: Boolean = false, locale: Locale = Locale.getDefault()): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val style = if (short) TextStyle.SHORT else TextStyle.FULL
        this.dayOfWeek.getDisplayName(style, locale)
    } else {
        // Fallback: format manually (non-localized)
        val name = this.dayOfWeek.name
        if (short) name.substring(0, 3).lowercase().replaceFirstChar { it.uppercase() }
        else name.lowercase().replaceFirstChar { it.uppercase() }
    }
}

fun Int.formatEpochDate(short: Boolean = true): String {
    val date = LocalDate.fromEpochDays(this)
    val month = date.monthFormatted(short)
    val dayOfWeek = date.dayOfWeekFormatted(short)

    return "$dayOfWeek, $month ${date.dayOfMonth}"
}
fun Int.getGraphLabel(): String {
    val date = LocalDate.fromEpochDays(this)
    val month = date.monthFormatted(true)
    return "$month ${date.dayOfMonth}"
}

/**
 * Long -> Formatted Date
 */

fun Long.formatTimestamp(short: Boolean = true): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val timeZone = TimeZone.currentSystemDefault()
    val localDate =  instant.toLocalDateTime(timeZone)

    val dateFormatted = localDate.date.toEpochDays().formatEpochDate(short)
    val timeFormatted = "${localDate.time.hour}:${localDate.time.minute}"

    return "$dateFormatted, $timeFormatted"
}













