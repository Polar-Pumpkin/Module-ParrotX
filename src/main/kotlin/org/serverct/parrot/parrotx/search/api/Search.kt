package org.serverct.parrot.parrotx.search.api

import org.serverct.parrot.parrotx.search.api.corrector.Corrector
import org.serverct.parrot.parrotx.search.api.exception.CorrectFailureException
import org.serverct.parrot.parrotx.search.api.exception.ExceedMaxTriesException
import org.serverct.parrot.parrotx.search.api.exception.ValidateFailureException
import org.serverct.parrot.parrotx.search.api.filter.Filter
import org.serverct.parrot.parrotx.search.api.provider.Provider
import taboolib.common.platform.function.submit
import java.util.concurrent.CompletableFuture

class Search<E>(builder: Search<E>.() -> Unit) {

    var maxTries = 3
    var timeout = 3
    private lateinit var provider: Provider<E>
    private val filters: MutableList<Filter<E>> = ArrayList()
    private val correctors: MutableList<Corrector<E>> = ArrayList()
    private var tries = 0

    init {
        builder()
    }

    fun by(provider: Provider<E>) {
        this.provider = provider
    }

    fun filter(filter: Filter<E>) = filters.add(filter)

    fun correct(corrector: Corrector<E>) = correctors.add(corrector)

    fun start(): CompletableFuture<E> {
        val future = CompletableFuture<E>()

        if (!this::provider.isInitialized) {
            future.completeExceptionally(IllegalStateException("Provider isn't initialized"))
            return future
        }
        search(future)

        if (timeout > 0) {
            submit(delay = timeout * 20L) {
                if (!future.isDone) {
                    future.completeExceptionally(ExceedMaxTriesException(timeout))
                }
            }
        }

        return future
    }

    private fun search(result: CompletableFuture<E>) {
        if (result.isDone) {
            return
        }
        if (tries++ >= maxTries) {
            result.completeExceptionally(ExceedMaxTriesException(maxTries))
            return
        }

        // FIXME 有比较严重的性能问题，怀疑为 future 的任务残留
        provider.generate().whenComplete queue@{ queue, exception ->
            if (result.isDone) {
                return@queue
            }
            if (exception != null) {
                result.completeExceptionally(exception)
                return@queue
            }

            while (!result.isDone && queue.isNotEmpty()) {
                val value = queue.poll()

                val correction = CompletableFuture<E>()
                correct(value, correctors.iterator(), correction)
                correction.whenComplete correct@{ corrected, failure ->
                    if (failure != null) {
                        failure.printStackTrace()
                        return@correct
                    }
                    if (corrected == null) {
                        NullPointerException("Corrected value is null").printStackTrace()
                        return@correct
                    }

                    val validation = CompletableFuture<Boolean>()
                    validate(corrected, filters.iterator(), validation)
                    validation.whenComplete { check, _ ->
                        if (check) {
                            result.complete(corrected)
                        }
                    }
                }
            }

            if (!result.isDone) {
                search(result)
            }
        }
    }

    private fun correct(value: E, iterator: Iterator<Corrector<E>>, correction: CompletableFuture<E>) {
        if (iterator.hasNext()) {
            val corrector = iterator.next()
            corrector.correct(value).whenComplete { corrected, exception ->
                submit {
                    if (exception != null) {
                        correction.completeExceptionally(CorrectFailureException(corrector, exception))
                        return@submit
                    }
                    correct(corrected, iterator, correction)
                }
            }
        } else {
            correction.complete(value)
        }
    }

    private fun validate(value: E, iterator: Iterator<Filter<E>>, validation: CompletableFuture<Boolean>) {
        if (iterator.hasNext()) {
            val filter = iterator.next()
            filter.predict(value).whenComplete { predict, exception ->
                submit {
                    if (exception != null) {
                        ValidateFailureException(filter, exception).printStackTrace()
                    }

                    if (predict != null && !predict) {
                        validation.complete(false)
                        return@submit
                    }
                    validate(value, iterator, validation)
                }
            }
        } else {
            validation.complete(true)
        }
    }

}