package com.yoloroy.model

import com.yoloroy.domain.booking.BookingModel
import com.yoloroy.domain.booking.BookingRepository
import com.yoloroy.lib.extensions.isIntersect
import com.yoloroy.lib.util.ResultOf
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.datetime.LocalDateTime

class BookingService(private val bookingRepository: BookingRepository) {
    private val observersInDuration = mutableListOf<Pair<ClosedRange<LocalDateTime>, () -> Unit>>()

    fun add(clientId: Int, tableId: Int, start: LocalDateTime, end: LocalDateTime): ResultOf<BookingModel> =
        bookingRepository.add(clientId, tableId, start, end).also {
            notifyInInterval(start, end)
        }

    fun cancel(bookingId: Int, clientId: Int): ResultOf<BookingModel> =
        bookingRepository.cancel(bookingId, clientId).also {
            (it as? ResultOf.Success)?.value
                ?.let { booking -> booking.start to booking.end }
                ?.let { (start, end) -> notifyInInterval(start, end) }
        }

    private fun notifyInInterval(start: LocalDateTime, end: LocalDateTime) {
        observersInDuration
            .filter { (interval, _) -> (start..end) isIntersect interval }
            .forEach { (_, action) -> action() }
    }

    fun observeInterval(interval: ClosedRange<LocalDateTime>, block: () -> Unit) = observersInDuration.add((interval to block))
}
