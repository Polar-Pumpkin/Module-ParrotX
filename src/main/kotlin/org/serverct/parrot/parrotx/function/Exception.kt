package org.serverct.parrot.parrotx.function

fun String.causedBy(cause: Throwable) {
    IllegalStateException(this, cause).printStackTrace()
}