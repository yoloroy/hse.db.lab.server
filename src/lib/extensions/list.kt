package com.yoloroy.lib.extensions

fun <T> MutableList<T>.removeAndReturnIfOrNull(predicate: (T) -> Boolean): T? {
    val index = indexOfFirst(predicate).takeUnless { it == -1 }
    return index?.let { removeAt(it) } ?: null
}
