package com.yoloroy.lib.extensions

import io.ktor.http.cio.websocket.*
import kotlinx.datetime.LocalDateTime

fun Frame.receivePairOfDates() =
    (this as Frame.Text).readText()
        .split(",")
        .map { LocalDateTime.parse(it) }
        .run { first() to last() }
