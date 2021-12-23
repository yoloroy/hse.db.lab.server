package com.yoloroy.domain.tables_status

import com.yoloroy.lib.util.ResultOf
import kotlinx.datetime.LocalDateTime

interface TablesStatusRepository {
    fun getAllForInterval(interval: ClosedRange<LocalDateTime>): ResultOf<List<TableStatusModel>>
}
