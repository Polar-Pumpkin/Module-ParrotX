package org.serverct.parrot.parrotx.builtin

open class BuiltinHash<T, V> : Builtin<T, V> {

    val map = HashMap<T, V>()

    override fun set(key: T, value: V) {
        map[key] = value
    }

    override fun getOrNull(key: T): V? {
        return map[key]
    }

    override fun remove(key: T): V {
        return map.remove(key)!!
    }

    override fun keys(): Set<T> {
        return map.keys
    }

    override fun values(): Collection<V> {
        return map.values
    }

    override fun entries(): Set<Map.Entry<T, V>> {
        return map.entries
    }

    override fun indexOf(key: T): Int {
        return keys().indexOf(key)
    }

    override fun clear() {
        map.clear()
    }

    override fun size(): Int {
        return map.size
    }

    override fun isEmpty(): Boolean {
        return map.isEmpty()
    }

    override fun isNotEmpty(): Boolean {
        return map.isNotEmpty()
    }

    override fun containsKey(key: T): Boolean {
        return map.containsKey(key)
    }

    override fun containsValue(value: V): Boolean {
        return map.containsValue(value)
    }
}
