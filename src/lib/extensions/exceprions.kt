package com.yoloroy.lib.extensions

inline fun <T> tryOrNull(block: () -> T) = try {
    block()
} catch (e: Exception) {
    println("TryOrNull: $e")
    null
}
