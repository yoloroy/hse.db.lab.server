package com.yoloroy.routing.view.waiter

import io.ktor.application.*
import io.ktor.html.*
import io.ktor.util.pipeline.*

suspend fun PipelineContext<Unit, ApplicationCall>.waiterView() {
    call.respondHtmlTemplate(WaiterTemplate()) {}
}
