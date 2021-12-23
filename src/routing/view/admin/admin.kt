package com.yoloroy.routing.view.admin

import com.yoloroy.model.TableService
import io.ktor.application.*
import io.ktor.html.*
import io.ktor.util.pipeline.*

suspend fun PipelineContext<Unit, ApplicationCall>.adminView(tableService: TableService) =
    call.respondHtmlTemplate(AdminTemplate(tableService.getAllStats())) {}
