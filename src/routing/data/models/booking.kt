package com.yoloroy.routing.data.models

import com.yoloroy.domain.booking.BookingModel
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookingDto(
    val id: Int,
    @SerialName("client_id") val clientId: Int,
    @SerialName("table_id") val tableId: Int,
    val start: LocalDateTime,
    val end: LocalDateTime,
) {
    constructor(model: BookingModel) : this(model.id, model.clientId, model.tableId, model.start, model.end)
}

@Serializable
data class BookingAddDto(
    @SerialName("client_id") val clientId: Int,
    @SerialName("table_id") val tableId: Int,
    val start: LocalDateTime,
    val end: LocalDateTime
)

@Serializable
data class BookingRemoveDto(
    @SerialName("booking_id") val bookingId: Int,
    @SerialName("client_id") val clientId: Int
)

@Serializable
data class BookingsGetDto(
    @SerialName("client_id") val clientId: Int
)
