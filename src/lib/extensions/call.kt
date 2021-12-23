package com.yoloroy.lib.extensions

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*

suspend fun ApplicationCall.respondWrongParam(name: String) = respond(HttpStatusCode.BadRequest, "Bad param: $name")
