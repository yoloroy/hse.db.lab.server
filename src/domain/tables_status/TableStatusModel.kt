package com.yoloroy.domain.tables_status

data class TableStatusModel(
    val tableId: Int,
    val bookingId: Int?,
    val isBooked: Boolean,
    val imageUrl: String?
)
