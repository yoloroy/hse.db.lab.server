package com.yoloroy.domain.sql.repository

import com.yoloroy.domain.client.ClientDao
import com.yoloroy.domain.client.ClientModel
import com.yoloroy.domain.client.ClientRepository
import com.yoloroy.lib.extensions.tryOrNull

class SQLClientRepository : ClientRepository {
    override fun add(name: String, discount: Double, ban: Boolean) =
        tryOrNull { ClientDao.add(name, discount, ban) }
            ?.let { ClientModel(it.id, it.name, it.discount, it.ban) }
            .let(::daoResult)

    override fun setBan(id: Int, ban: Boolean) =
        tryOrNull { ClientDao.setBan(id, ban) }.let(::daoResult)

    override fun setDiscount(id: Int, discount: Double) =
        tryOrNull { ClientDao.setDiscount(id, discount) }.let(::daoResult)
}
