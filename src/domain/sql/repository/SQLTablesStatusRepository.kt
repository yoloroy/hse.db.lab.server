package com.yoloroy.domain.sql.repository

import com.yoloroy.domain.tables_status.TableStatusModel
import com.yoloroy.domain.tables_status.TablesStatusDao
import com.yoloroy.domain.tables_status.TablesStatusRepository
import com.yoloroy.lib.extensions.tryOrNull
import kotlinx.datetime.LocalDateTime

class SQLTablesStatusRepository : TablesStatusRepository {
    override fun getAllForInterval(interval: ClosedRange<LocalDateTime>) =
        tryOrNull { TablesStatusDao.getAllForInterval(interval.start, interval.endInclusive) }
            ?.map { TableStatusModel(it.tableId, it.bookingId, it.isBooked, it.imageUrl) }
            .let(::daoResult)
}
