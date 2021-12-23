package com.yoloroy.domain.table

import kotlinx.datetime.Month

data class TableModel(
    val id: Int,
    val minimumCheck: Double,
    val humanCapacity: Int
)

data class TableStatsModel(
    val id: Int,
    val minimumCheck: Double,
    val humanCapacity: Int,
    val monthlyUsage: Map<Month, Int>,
    val hoursUsage: Map<Int, Double>
)
