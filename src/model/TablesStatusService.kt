package com.yoloroy.model

import com.yoloroy.domain.tables_status.TablesStatusRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.datetime.LocalDateTime

@ExperimentalCoroutinesApi
class TablesStatusService(
    private val tablesStatusRepository: TablesStatusRepository,
    private val bookingService: BookingService,
) {
    fun subscribeOnInterval(interval: ClosedRange<LocalDateTime>, cancellationSignal: Flow<Unit>) =
        channelFlow {
            send(tablesStatusRepository.getAllForInterval(interval))

            bookingService.observeInterval(interval) {
                launch {
                    send(tablesStatusRepository.getAllForInterval(interval))
                }
            }

            cancellationSignal.collect { this@channelFlow.cancel() }
            awaitClose()
        }
}
