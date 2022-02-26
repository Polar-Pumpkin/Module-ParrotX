package org.serverct.parrot.parrotx.search.api.provider

import java.util.*
import java.util.concurrent.CompletableFuture

fun interface Provider<E> {
    fun generate(): CompletableFuture<Queue<E>>
}