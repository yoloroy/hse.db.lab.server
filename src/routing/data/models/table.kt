package com.yoloroy.routing.data.models

import com.yoloroy.domain.table.TableModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TableAddingDto(
    @SerialName("image_url") val imageUrl: String?,
    @SerialName("minimum_check") val minimumCheck: Double,
    @SerialName("client_capacity") val clientCapacity: Int
)

@Serializable
data class TableDeletingDto(
    val id: Int
)

@Serializable
data class TableDto(
    val id: Int,
    @SerialName("image_url") val imageUrl: String?,
    @SerialName("minimum_check") val minimumCheck: Double,
    @SerialName("client_capacity") val clientCapacity: Int
) {
    constructor(tm: TableModel) : this(tm.id, tm.imageUrl, tm.minimumCheck, tm.humanCapacity)
}
