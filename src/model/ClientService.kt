package com.yoloroy.model

import com.yoloroy.domain.client.ClientRepository

class ClientService(private val clientRepository: ClientRepository) {
    // TODO add observing

    fun add(name: String, discount: Double = .0, ban: Boolean = false) =
        clientRepository.add(name, discount, ban)

    fun setBan(id: Int, ban: Boolean) =
        clientRepository.setBan(id, ban)

    fun setDiscount(id: Int, discount: Double) =
        clientRepository.setDiscount(id, discount)
}
