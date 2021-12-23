package com.yoloroy.domain.sql.repository

import com.yoloroy.domain.booking.BookingDao
import com.yoloroy.domain.booking.BookingModel
import com.yoloroy.domain.booking.BookingRepository
import com.yoloroy.lib.extensions.tryOrNull
import com.yoloroy.lib.util.ResultOf
import kotlinx.datetime.LocalDateTime

class SQLBookingRepository : BookingRepository {
    override fun add(clientId: Int, tableId: Int, start: LocalDateTime, end: LocalDateTime) =
        tryOrNull { BookingDao.add(clientId, tableId, start, end) }
            ?.let { BookingModel(it.id, it.clientId, it.tableId, it.start, it.end) }
            .let(::daoResult)

    override fun cancel(bookingId: Int, clientId: Int) =
        tryOrNull { BookingDao.cancel(bookingId, clientId) }
            ?.let { BookingModel(it.id, it.clientId, it.tableId, it.start, it.end) }
            .let(::daoResult)
}
