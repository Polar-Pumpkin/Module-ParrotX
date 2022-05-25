package org.serverct.parrot.parrotx.container

abstract class Registry<K, V> : Map<K, V> {

    abstract val registered: MutableMap<K, V>

    abstract fun register(value: V, force: Boolean = false)

    open fun register(force: Boolean = false, value: () -> V): Result<V> {
        return runCatching {
            value()
        }.onSuccess {
            register(it, force)
        }
    }

    open fun unregister(key: K): V? = registered.remove(key)

    open fun unregisterIf(predicate: (Map.Entry<K, V>) -> Boolean): Boolean {
        val before = size
        registered.entries.filter(predicate).forEach { (key, _) ->
            registered.remove(key)
        }
        return size < before
    }

    override val size: Int
        get() = registered.size
    override val keys: Set<K>
        get() = registered.keys
    override val values: Collection<V>
        get() = registered.values
    override val entries: Set<Map.Entry<K, V>>
        get() = registered.entries

    override fun get(key: K): V? = registered[key]

    override fun isEmpty(): Boolean = registered.isEmpty()

    override fun containsKey(key: K): Boolean = registered.containsKey(key)

    override fun containsValue(value: V): Boolean = registered.containsValue(value)

}

abstract class SimpleRegistry<K, V> : Registry<K, V>() {

    abstract val V.key: K

    override fun register(value: V, force: Boolean) {
        check(!force && value.key in registered) { "尝试向 ${this::class.java.simpleName} 重复注册 ${value.key}" }
        registered[value.key] = value
    }

}