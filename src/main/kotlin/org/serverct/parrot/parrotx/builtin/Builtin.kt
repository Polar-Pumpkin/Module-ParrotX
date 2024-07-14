package org.serverct.parrot.parrotx.builtin

interface Builtin<T, V> {

    operator fun set(key: T, value: V)

    operator fun get(key: T): V {
        return getOrNull(key) ?: throw NoSuchElementException("Key $key not found")
    }

    fun getOrNull(key: T): V?

    fun remove(key: T): V

    fun keys(): Set<T>

    fun values(): Collection<V>

    fun entries(): Set<Map.Entry<T, V>>

    fun indexOf(key: T): Int

    fun clear()

    fun size(): Int

    fun isEmpty(): Boolean

    fun isNotEmpty(): Boolean

    fun containsKey(key: T): Boolean

    fun containsValue(value: V): Boolean

}
