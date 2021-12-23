package com.yoloroy.domain.booking

import com.yoloroy.domain.sql.models.BookingRow
import com.yoloroy.lib.extensions.toSQLTimeStamp
import com.yoloroy.lib.util.toKotlinLocalDateTime
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.ResultSet

object BookingDao {

    fun add(clientId: Int, tableId: Int, start: LocalDateTime, end: LocalDateTime): BookingRow? = transaction {
        exec(
            """select booking_id, client_id, table_id, start_time, end_time
              |from booking_add($clientId, $tableId, ${start.toSQLTimeStamp()}, ${end.toSQLTimeStamp()})""".trimMargin()
        ) {
            require(it.next())
            it.getBooking()
        }
    }

    fun cancel(bookingId: Int, clientId: Int): BookingRow? = transaction {
        exec("select booking_id, client_id, table_id, start_time, end_time from booking_delete($bookingId, $clientId)") {
            require(it.next())
            it.getBooking()
        }
    }
}

private fun ResultSet.getBooking() = BookingRow(
    getInt("booking_id"),
    getInt("client_id"),
    getInt("table_id"),
    getTimestamp("start_time").toKotlinLocalDateTime(),
    getTimestamp("end_time").toKotlinLocalDateTime()
)
