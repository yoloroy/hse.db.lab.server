package com.yoloroy.lib.util

sealed class ResultOf<out T> {
    data class Success<out T>(val value: T): ResultOf<T>() {
        override fun <R> transformTo(block: (T) -> R): ResultOf<R> = try {
            Success(block(value))
        } catch (e: Exception) {
            Error("Result transform error", e)
        }
    }

    data class Error(val message: String?, val throwable: Throwable?): ResultOf<Nothing>() {
        override fun <R> transformTo(block: (Nothing) -> R) = Error("$message + Result transform error", throwable)
    }

    abstract infix fun <R> transformTo(block: (T) -> R): ResultOf<R>

    companion object {
        operator fun <T> invoke(value: T?, errorMessage: String?, throwable: Throwable?) =
            value?.let { Success(it) } ?: Error(errorMessage, throwable)
    }
}
