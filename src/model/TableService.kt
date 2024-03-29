package com.yoloroy.model

import com.yoloroy.domain.table.TableRepository

class TableService(private val tableRepository: TableRepository) {
    fun add(imageUrl: String?, minimumCheck: Double, humanCapacity: Int) =
        tableRepository.add(imageUrl, minimumCheck, humanCapacity)

    fun getAll() = tableRepository.getAll()

    fun getAllStats() = tableRepository.getAllStats()

    fun update(id: Int, minimumCheck: Double? = null, humanCapacity: Int? = null) =
        tableRepository.update(id, minimumCheck, humanCapacity)

    fun delete(id: Int) = tableRepository.delete(id)
}
