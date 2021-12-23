package com.yoloroy.routing.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.yoloroy.domain.tables_status.TableStatusModel as TableStatusModel1

@Serializable
data class TableStatusDto(
    @SerialName("table_id") val tableId: Int,
    @SerialName("booking_id") val bookingId: Int?,
    @SerialName("is_booked") val isBooked: Boolean
) {
    constructor(model: TableStatusModel1) : this(model.tableId, model.bookingId, model.isBooked)
}
