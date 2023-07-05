@file:Suppress("unused")

package org.serverct.parrot.parrotx.function

import taboolib.common.platform.function.info

tailrec fun <E, V> toEach(value: V, elements: Iterator<E>, function: E.(V) -> V): V =
    if (!elements.hasNext()) value else toEach(function(elements.next(), value), elements, function)

fun <E, V> Iterable<E>.toEach(value: V, function: E.(V) -> V): V = toEach(value, iterator(), function)

fun <E, V> Map<*, E>.toEach(value: V, function: E.(V) -> V): V = toEach(value, values.iterator(), function)

inline fun <V> timeElapsed(name: String, func: () -> V): V {
    val timestamp = System.currentTimeMillis()
    return func().also {
        info("$name(${System.currentTimeMillis() - timestamp}ms)")
    }
}