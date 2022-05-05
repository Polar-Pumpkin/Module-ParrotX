package org.serverct.parrot.parrotx.container

abstract class LazyMap<K, V> : Map<K, V> {

    abstract val loaded: MutableMap<K, V>

    abstract fun load(key: K): V?

    override val size: Int
        get() = loaded.size
    override val keys: Set<K>
        get() = loaded.keys
    override val values: Collection<V>
        get() = loaded.values
    override val entries: Set<Map.Entry<K, V>>
        get() = loaded.entries

    override fun get(key: K): V? {
        if (key in loaded) {
            return loaded[key]
        }
        return load(key)?.also {
            loaded[key] = it
        }
    }

    override fun isEmpty(): Boolean = loaded.isEmpty()

    override fun containsKey(key: K): Boolean = loaded.containsKey(key)

    override fun containsValue(value: V): Boolean = loaded.containsValue(value)

}