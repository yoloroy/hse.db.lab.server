package com.yoloroy.domain.client

import com.yoloroy.lib.util.ResultOf
import com.yoloroy.domain.client.ClientModel
import com.yoloroy.domain.client.ClientDao

interface ClientRepository {
    fun add(name: String, discount: Double, ban: Boolean): ResultOf<ClientModel>

    fun setBan(id: Int, ban: Boolean): ResultOf<Boolean>

    fun setDiscount(id: Int, discount: Double): ResultOf<Boolean>
}
