package com.yoloroy.domain.sql.repository

import com.yoloroy.domain.sql.models.TableHoursUsageRow
import com.yoloroy.domain.sql.models.TableMonthsUsageRow
import com.yoloroy.domain.sql.models.TableRow
import com.yoloroy.domain.table.TableDao
import com.yoloroy.domain.table.TableModel
import com.yoloroy.domain.table.TableRepository
import com.yoloroy.domain.table.TableStatsModel
import com.yoloroy.lib.extensions.tryOrNull
import kotlinx.datetime.Month

class SQLTableRepository : TableRepository {
    override fun add(imageUrl: String?, minimumCheck: Double, humanCapacity: Int) =
        tryOrNull { TableDao.add(imageUrl, minimumCheck, humanCapacity) }
            ?.let { TableModel(it.id, it.minimumCheck, it.humanCapacity, it.imageUrl) }
            .let(::daoResult)

    override fun getAll() =
        tryOrNull { TableDao.getAll() }
            ?.map { TableModel(it.id, it.minimumCheck, it.humanCapacity, it.imageUrl) }
            .let(::daoResult)

    override fun update(id: Int, minimumCheck: Double?, humanCapacity: Int?) =
        tryOrNull { TableDao.update(id, minimumCheck, humanCapacity) }
            ?.let { TableModel(it.id, it.minimumCheck, it.humanCapacity, it.imageUrl) }
            .let(::daoResult)

    override fun delete(id: Int) =
        tryOrNull { TableDao.delete(id) }
            ?.let { TableModel(it.id, it.minimumCheck, it.humanCapacity, it.imageUrl) }
            .let(::daoResult)

    override fun getAllStats() =
        tryOrNull { TableDao.getStats() }
            ?.run { tableStatsModels(first, second, third) }
            .let(::daoResult)

    private fun tableStatsModels(
        first: List<TableRow>,
        second: List<TableMonthsUsageRow>,
        third: List<TableHoursUsageRow>
    ) = first.map { table ->
        TableStatsModel(
            table.id, table.minimumCheck, table.humanCapacity,
            second
                .filter { it.tableId == table.id }
                .associate { Month(it.month) to it.usage },
            third
                .filter { it.tableId == table.id }
                .associate { it.hour to it.averageUsage },
            table.imageUrl
        )
    }
}
