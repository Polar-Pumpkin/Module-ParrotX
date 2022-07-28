@file:Isolated
@file:Suppress("unused")

package org.serverct.parrot.parrotx.function

import taboolib.common.Isolated

fun Throwable.print(reason: String, exception: (String, Throwable) -> Throwable = { _reason, ex -> IllegalStateException(_reason, ex) }) {
    exception(reason, this).printStackTrace()
}

fun String.causedBy(cause: Throwable) {
    cause.print(this)
}

infix fun Throwable.because(reason: String) {
    reason.causedBy(this)
}