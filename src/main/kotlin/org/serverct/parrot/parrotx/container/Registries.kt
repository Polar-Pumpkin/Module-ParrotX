@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package org.serverct.parrot.parrotx.container

import java.util.function.Predicate

abstract class Registry<K, V>(val registered: MutableMap<K, V>) : Map<K, V> by registered {
    open fun transformKey(key: K): K = key

    open fun register(key: K, value: V, force: Boolean = false) {
        requireNotNull(value) { "尝试向 ${this::class.java.canonicalName} 注册空值" }

        val transformed = transformKey(key)
        require(force || transformed !in registered) { "尝试向 ${this::class.java.canonicalName} 重复注册 $key" }
        registered[transformed] = value
    }

    abstract fun register(value: V, force: Boolean = false)

    open fun register(force: Boolean = false, value: () -> V): Result<V> {
        return runCatching {
            value()
        }.onSuccess {
            register(it, force)
        }
    }

    override fun get(key: K): V? = registered[transformKey(key)]

    open fun unregister(key: K): V? = registered.remove(key)

    open fun unregisterIf(predicate: (Map.Entry<K, V>) -> Boolean): Boolean {
        val before = size
        registered.entries.filter(predicate).forEach { (key, _) ->
            registered.remove(key)
        }
        return size < before
    }

    fun unregisterIf(predicate: Predicate<Map.Entry<K, V>>): Boolean = unregisterIf { predicate.test(it) }
}

abstract class SimpleRegistry<K, V>(source: MutableMap<K, V>) : Registry<K, V>(source) {
    abstract val V.key: K

    override fun register(value: V, force: Boolean) = register(value.key, value, force)
}

abstract class GenericRegistry<K, V>(source: MutableMap<K, V>) : Registry<K, V>(source) {
    abstract fun extract(value: V): K

    override fun register(value: V, force: Boolean) = register(extract(value), value, force)
}