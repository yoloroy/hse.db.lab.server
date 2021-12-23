package com.yoloroy.routing.view.client

import io.ktor.application.*
import io.ktor.html.*
import io.ktor.util.pipeline.*

suspend fun PipelineContext<Unit, ApplicationCall>.clientView() {
    call.respondHtmlTemplate(ClientTemplate()) {}
}
