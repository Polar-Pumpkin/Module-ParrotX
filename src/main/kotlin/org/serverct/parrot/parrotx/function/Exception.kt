package org.serverct.parrot.parrotx.function

fun Throwable.causedBy(message: String) {
    IllegalStateException(message, this).printStackTrace()
}