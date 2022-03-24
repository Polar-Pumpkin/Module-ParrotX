package org.serverct.parrot.parrotx.container

abstract class DatabaseStorage<T : Enum<T>> {

    fun column(value: T): String = value.name.lowercase()

}