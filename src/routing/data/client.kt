package com.yoloroy.routing.data

import com.yoloroy.model.ClientService
import com.yoloroy.routing.data.models.ClientAddingDto
import com.yoloroy.routing.data.models.ClientBanDto
import com.yoloroy.routing.data.models.ClientDiscountDto
import com.yoloroy.routing.data.models.ClientDto
import io.ktor.application.*
import io.ktor.util.pipeline.*

suspend fun PipelineContext<Unit, ApplicationCall>.addClient(clientService: ClientService) =
    resultMethod<ClientAddingDto> { dto ->
        println(dto)
        clientService.add(dto.name, dto.discount, dto.ban) transformTo ::ClientDto
    }

suspend fun PipelineContext<Unit, ApplicationCall>.updateClientDiscount(clientService: ClientService) =
    resultMethod<ClientDiscountDto> { dto ->
        clientService.setDiscount(dto.id, dto.discount)
    }

suspend fun PipelineContext<Unit, ApplicationCall>.updateClientBan(clientService: ClientService) =
    resultMethod<ClientBanDto> { dto ->
        clientService.setBan(dto.id, dto.ban)
    }
