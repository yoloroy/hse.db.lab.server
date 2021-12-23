package com.yoloroy.routing.data

import com.yoloroy.lib.util.ResultOf
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.pipeline.*

suspend inline fun <reified V: Any> PipelineContext<Unit, ApplicationCall>.resultMethod(crossinline transform: (V) -> ResultOf<Any>) {
    when (
        val result = transform(call.receive(V::class))
    ) {
        is ResultOf.Success<*> -> call.respond(result.value!!)
        is ResultOf.Error -> call.respond(HttpStatusCode.InternalServerError, "$result")
    }
}
