package com.yoloroy.domain.booking

import kotlinx.datetime.LocalDateTime

data class BookingModel(
    val id: Int,
    val clientId: Int,
    val tableId: Int,
    val start: LocalDateTime,
    val end: LocalDateTime
)
