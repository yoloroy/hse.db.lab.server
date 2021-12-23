package com.yoloroy.lib.extensions

infix fun <T: Comparable<T>> ClosedRange<T>.isIntersect(other: ClosedRange<T>) =
    maxOf(start, other.start) < minOf(endInclusive, other.endInclusive)
