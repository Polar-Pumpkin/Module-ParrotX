package org.serverct.parrot.parrotx.search.api.provider

import java.util.*
import java.util.concurrent.CompletableFuture

abstract class LazyProvider<E> : Provider<E> {

    abstract fun create(): Queue<E>

    private val value by lazy { create() }

    override fun generate(): CompletableFuture<Queue<E>> = CompletableFuture.completedFuture(LinkedList(value))

}