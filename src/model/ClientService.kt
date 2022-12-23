package com.yoloroy.model

import com.yoloroy.domain.client.ClientRepository
import com.yoloroy.lib.util.ResultOf

class ClientService(
    private val clientRepository: ClientRepository,
    private val activationCodeGenerator: ActivationCodeGenerator = ActivationCodeGenerator { randomCode() }
) {
    // TODO add observing

    fun add(name: String, discount: Double = .0, ban: Boolean = false) = clientRepository.add(name, discount, ban)

    fun check(name: String, phone: String) = ResultOf.Success(activationCodeGenerator.generate())

    fun setBan(id: Int, ban: Boolean) = clientRepository.setBan(id, ban)

    fun setDiscount(id: Int, discount: Double) = clientRepository.setDiscount(id, discount)

    fun interface ActivationCodeGenerator {
        fun generate(): Int
    }
}

fun randomCode() = (10000..99999).random()
