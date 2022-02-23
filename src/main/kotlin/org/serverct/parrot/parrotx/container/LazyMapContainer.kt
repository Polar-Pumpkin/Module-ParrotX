package org.serverct.parrot.parrotx.container

abstract class LazyMapContainer<K, V> : org.serverct.parrot.parrotx.container.DelegateMapContainer<K, V>() {

    abstract fun load(key: K): V?

    override fun get(key: K): V? {
        if (key in container) {
            return container[key]
        }
        return load(key)?.also { container[key] = it }
    }

}