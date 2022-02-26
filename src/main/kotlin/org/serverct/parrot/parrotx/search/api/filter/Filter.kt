package org.serverct.parrot.parrotx.search.api.filter

import java.util.concurrent.CompletableFuture

fun interface Filter<E> {
    fun predict(value: E): CompletableFuture<Boolean>
}