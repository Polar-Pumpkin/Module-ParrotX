@file:Isolated
@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package org.serverct.parrot.parrotx.container

import com.google.common.collect.Range
import taboolib.common.Isolated
import java.util.*

abstract class LazyMap<K, V>(val loaded: MutableMap<K, V> = HashMap()) : Map<K, V> by loaded {
    abstract fun load(key: K): V?

    override fun get(key: K): V? {
        if (key in loaded) {
            return loaded[key]
        }
        return load(key)?.also {
            loaded[key] = it
        }
    }
}

class MilestoneMap<K : Comparable<K>, V>(
    val milestones: NavigableMap<K, V> = TreeMap(),
    var extend: Extend = Extend.LEFT
) : NavigableMap<K, V> by milestones {
    val minimum: Map.Entry<K, V>?
        get() = firstEntry()
    val maximum: Map.Entry<K, V>?
        get() = lastEntry()
    val range: Range<K>?
        get() = if (isEmpty()) null else Range.closed(minimum!!.key, maximum!!.key)

    override fun get(key: K): V? {
        return when {
            milestones.isEmpty() -> null
            milestones.contains(key) -> milestones[key]
            else -> when (extend) {
                Extend.NONE -> null
                Extend.LEFT -> lowerEntry(key).value
                Extend.RIGHT -> higherEntry(key).value
            }
        }
    }

    override fun containsKey(key: K): Boolean = when {
        isEmpty() -> false
        extend == Extend.NONE -> milestones.contains(key)
        else -> range!!.contains(key)
    }

    enum class Extend {
        LEFT, RIGHT, NONE;
    }
}