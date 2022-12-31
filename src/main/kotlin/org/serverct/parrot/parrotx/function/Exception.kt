@file:Isolated
@file:Suppress("unused")

package org.serverct.parrot.parrotx.function

import taboolib.common.Isolated
import taboolib.common.platform.function.warning

@Deprecated("Duplicate stack trace", ReplaceWith("this because reason"))
fun Throwable.print(reason: String, exception: (String, Throwable) -> Throwable = { _reason, ex -> IllegalStateException(_reason, ex) }) {
    // exception(reason, this).printStackTrace()
    this because reason
}

fun String.causedBy(cause: Throwable) {
    // cause.print(this)
    warning(this)
    cause.printStackTrace()
}

infix fun Throwable.because(reason: String) {
    reason.causedBy(this)
}