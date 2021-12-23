package com.yoloroy.domain.booking

import com.yoloroy.lib.util.ResultOf
import kotlinx.datetime.LocalDateTime

interface BookingRepository {
    fun add(clientId: Int, tableId: Int, start: LocalDateTime, end: LocalDateTime): ResultOf<BookingModel>

    fun cancel(bookingId: Int, clientId: Int): ResultOf<BookingModel>
}
