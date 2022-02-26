package org.serverct.parrot.parrotx.search.api.corrector

import java.util.concurrent.CompletableFuture

fun interface Corrector<E> {
    fun correct(value: E): CompletableFuture<E>
}