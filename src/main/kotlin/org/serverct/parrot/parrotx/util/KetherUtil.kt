package org.serverct.parrot.parrotx.util

import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

object KetherUtil {

    fun <V> collect(frame: ScriptFrame, actions: List<ParsedAction<*>>): CompletableFuture<List<V>> {
        val future = CompletableFuture<List<V>>()
        collect(frame, future, 0, actions, ArrayList())
        return future
    }

    private fun <V> collect(
        frame: ScriptFrame,
        future: CompletableFuture<List<V>>,
        cursor: Int,
        actions: List<ParsedAction<*>>,
        collector: ArrayList<V>
    ) {
        if (cursor < actions.size) {
            frame.newFrame(actions[cursor]).run<V>().thenApply { value ->
                collector.add(value)
                collect(frame, future, cursor + 1, actions, collector)
            }
        } else {
            future.complete(collector)
        }
    }

}