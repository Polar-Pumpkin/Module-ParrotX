package org.serverct.parrot.parrotx.function

import java.math.RoundingMode

fun Int.next(range: IntRange): Int {
    var next = this + 1
    if (next > range.last) {
        next = range.first
    }
    return next
}

fun Double.round(scale: Int = 2): Double = toBigDecimal().setScale(scale, RoundingMode.HALF_DOWN).toDouble()