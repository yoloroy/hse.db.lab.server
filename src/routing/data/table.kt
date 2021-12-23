package com.yoloroy.routing.data

import com.yoloroy.model.TableService
import com.yoloroy.routing.data.models.TableAddingDto
import com.yoloroy.routing.data.models.TableDeletingDto
import com.yoloroy.routing.data.models.TableDto
import io.ktor.application.*
import io.ktor.util.pipeline.*

suspend fun PipelineContext<Unit, ApplicationCall>.addTable(tableService: TableService) =
    resultMethod<TableAddingDto> {
        tableService.add(it.minimumCheck, it.clientCapacity) transformTo ::TableDto
    }

suspend fun PipelineContext<Unit, ApplicationCall>.deleteTable(tableService: TableService) =
    resultMethod<TableDeletingDto> {
        tableService.delete(it.id) transformTo ::TableDto
    }
