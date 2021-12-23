package com.yoloroy.domain.table

import com.yoloroy.lib.util.ResultOf

interface TableRepository {
    fun add(minimumCheck: Double, humanCapacity: Int): ResultOf<TableModel>

    fun getAll(): ResultOf<List<TableModel>>

    fun update(id: Int, minimumCheck: Double? = null, humanCapacity: Int? = null): ResultOf<TableModel>

    fun delete(id: Int): ResultOf<TableModel>

    fun getAllStats(): ResultOf<List<TableStatsModel>>
}
