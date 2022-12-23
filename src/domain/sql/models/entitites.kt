package com.yoloroy.domain.sql.models

import kotlinx.datetime.LocalDateTime
import java.util.*

data class ClientRow(
    val id: Int,
    val name: String,
    val discount: Double,
    val ban: Boolean,
)

data class TableRow(
    val id: Int,
    val minimumCheck: Double,
    val humanCapacity: Int,
    val imageUrl: String?
)

data class BookingRow(
    val id: Int,
    val clientId: Int,
    val tableId: Int,
    val start: LocalDateTime,
    val end: LocalDateTime,
)

data class TableStatusRow(
    val tableId: Int,
    val isBooked: Boolean,
    val bookingId: Int?,
    val imageUrl: String?
)

data class TableHoursUsageRow(
    val tableId: Int,
    val hour: Int,
    val averageUsage: Double
)

data class TableMonthsUsageRow(
    val tableId: Int,
    val month: Int,
    val usage: Int
)
