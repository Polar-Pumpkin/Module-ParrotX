package org.serverct.parrot.parrotx.container

abstract class Registry<K, V> : Map<K, V> {

    abstract val registered: MutableMap<K, V>

    abstract fun register(value: V, force: Boolean = false)

    abstract fun register(force: Boolean = false, value: () -> V): Result<V>

    abstract fun unregister(key: K): V?

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
        if (!force && value.key in registered) {
            IllegalStateException("尝试向 ${this::class.simpleName} 重复注册 ${value.key}").printStackTrace()
            return
        }
        registered[value.key] = value
    }

    override fun register(force: Boolean, value: () -> V): Result<V> {
        return runCatching {
            value()
        }.onSuccess {
            register(it, force)
        }
    }

    override fun unregister(key: K): V? = registered.remove(key)

}