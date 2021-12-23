package com.yoloroy.routing.data

import com.yoloroy.model.BookingService
import com.yoloroy.routing.data.models.BookingAddDto
import com.yoloroy.routing.data.models.BookingDto
import com.yoloroy.routing.data.models.BookingRemoveDto
import com.yoloroy.routing.data.models.BookingsGetDto
import io.ktor.application.*
import io.ktor.util.pipeline.*

suspend fun PipelineContext<Unit, ApplicationCall>.addBooking(bookingService: BookingService) =
    resultMethod<BookingAddDto> { dto ->
        bookingService.add(dto.clientId, dto.tableId, dto.start, dto.end) transformTo ::BookingDto
    }

suspend fun PipelineContext<Unit, ApplicationCall>.cancelBooking(bookingService: BookingService) =
    resultMethod<BookingRemoveDto> { dto ->
        bookingService.cancel(dto.bookingId, dto.clientId) transformTo ::BookingDto
    }
