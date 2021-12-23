package com.yoloroy.routing.data.models

import com.yoloroy.domain.client.ClientModel
import kotlinx.serialization.Serializable

@Serializable
data class ClientDto(
    val id: Int,
    val name: String,
    val discount: Double,
    val ban: Boolean
) {
    constructor(model: ClientModel) : this(model.id, model.name, model.discount, model.ban)
}

@Serializable
data class ClientAddingDto(
    val name: String,
    val discount: Double = .0,
    val ban: Boolean = false,
)

@Serializable
data class ClientDiscountDto(
    val id: Int,
    val discount: Double
)

@Serializable
data class ClientBanDto(
    val id: Int,
    val ban: Boolean
)
