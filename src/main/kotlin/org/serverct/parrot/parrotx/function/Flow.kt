@file:Suppress("unused")

package org.serverct.parrot.parrotx.function

tailrec fun <E, V> toEach(value: V, elements: Iterator<E>, function: E.(V) -> V): V =
    if (!elements.hasNext()) value else toEach(function(elements.next(), value), elements, function)

fun <E, V> Iterable<E>.toEach(value: V, function: E.(V) -> V): V = toEach(value, iterator(), function)

fun <E, V> Map<*, E>.toEach(value: V, function: E.(V) -> V): V = toEach(value, values.iterator(), function)