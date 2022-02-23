package org.serverct.parrot.parrotx.container

import taboolib.common.platform.function.warning

abstract class UniqueMapRegistry<K, V> : DelegateMapContainer<K, V>() {

    abstract val V.key: K

    fun register(value: V, force: Boolean = false) {
        if (!force && value.key in this) {
            warning("[${this::class.simpleName}] 尝试重复注册: ${value.key}")
            return
        }
        this[value.key] = value
    }

}