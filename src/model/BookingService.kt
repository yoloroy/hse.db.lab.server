package com.yoloroy.model

import com.yoloroy.domain.booking.BookingModel
import com.yoloroy.domain.booking.BookingRepository
import com.yoloroy.lib.extensions.isIntersect
import com.yoloroy.lib.util.ResultOf
import kotlinx.datetime.LocalDateTime

class BookingService(private val bookingRepository: BookingRepository) {
    private val observersInDuration = mutableListOf<Pair<ClosedRange<LocalDateTime>, () -> Unit>>()

    fun add(clientId: Int, tableId: Int, start: LocalDateTime, end: LocalDateTime) =
        bookingRepository.add(clientId, tableId, start, end).also { println(it) }.also {
            observersInDuration
                .filter { (interval, _) -> (start..end) isIntersect interval }
                .forEach { (_, action) -> action() }
        }

    fun cancel(bookingId: Int, clientId: Int): ResultOf<BookingModel> =
        bookingRepository.cancel(bookingId, clientId).also {
            (it as? ResultOf.Success)?.also { println(it) }?.value?.let { booking ->
                observersInDuration
                    .filter { (interval, _) -> (booking.start..booking.end) isIntersect interval }
                    .forEach { (_, action) -> action() }
            }
        }

    fun observeInterval(interval: ClosedRange<LocalDateTime>, block: () -> Unit) = observersInDuration.add((interval to block))
}
