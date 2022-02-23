package org.serverct.parrot.parrotx.function

fun Int.next(range: IntRange): Int {
    var next = this + 1
    if (next > range.last) {
        next = range.first
    }
    return next
}