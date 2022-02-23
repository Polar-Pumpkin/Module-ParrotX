package org.serverct.parrot.parrotx.container

abstract class DelegateMapContainer<K, V> : MutableMap<K, V> {

    abstract val container: MutableMap<K, V>

    override val size: Int
        get() = container.size

    override fun containsKey(key: K): Boolean = container.containsKey(key)

    override fun containsValue(value: V): Boolean = container.containsValue(value)

    override fun get(key: K): V? = container[key]

    override fun isEmpty(): Boolean = container.isEmpty()

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = container.entries
    override val keys: MutableSet<K>
        get() = container.keys
    override val values: MutableCollection<V>
        get() = container.values

    override fun clear() = container.clear()

    override fun put(key: K, value: V): V? = container.put(key, value)

    override fun putAll(from: Map<out K, V>) = container.putAll(from)

    override fun remove(key: K): V? = container.remove(key)

}