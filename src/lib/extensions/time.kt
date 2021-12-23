package com.yoloroy.lib.extensions

import kotlinx.datetime.*
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

fun LocalDateTime.Companion.now() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

@ExperimentalTime
operator fun LocalDateTime.plus(hours: Duration) =
    toJavaLocalDateTime().plusHours(hours.toLong(DurationUnit.HOURS)).toKotlinLocalDateTime()

fun LocalDateTime.toSQLTimeStamp() = "timestamp '${toString().replace('T', ' ')}'"
