package com.yoloroy.domain.tables_status

import com.yoloroy.domain.sql.models.TableStatusRow
import com.yoloroy.lib.extensions.toSQLTimeStamp
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.ZoneOffset

object TablesStatusDao {

    fun getAllForInterval(start: LocalDateTime, end: LocalDateTime): List<TableStatusRow>? = transaction {
        exec("select * from tables_status_get_all_for_interval(${start.toSQLTimeStamp()}, ${end.toSQLTimeStamp()})") {
            sequence {
                while (it.next()) {
                    yield(it.getTableStatus())
                }
            }.toList()
        }
    }
}

private fun ResultSet.getTableStatus() =
    TableStatusRow(getInt(1), getBoolean(2), getInt(3))
