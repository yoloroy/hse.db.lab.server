package com.yoloroy.lib.util

import kotlinx.serialization.json.Json.Default.decodeFromString
import kotlinx.serialization.json.JsonObject

fun String.textResource(work: (String?) -> Unit) {
    val content = javaClass::class.java.getResource(this)?.readText()
    work(content)
}

fun String.jsonResource(work: (JsonObject?) -> Unit) = textResource { text ->
    work(text?.let {
        decodeFromString(JsonObject.serializer(), it)
    })
}
