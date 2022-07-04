package org.serverct.parrot.parrotx.function

fun Throwable.print(reason: String, exception: (String, Throwable) -> Throwable = { _reason, ex -> IllegalStateException(_reason, ex) }) {
    exception(reason, this).printStackTrace()
}

fun String.causedBy(cause: Throwable) {
    cause.print(this)
}