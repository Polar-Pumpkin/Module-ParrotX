@file:Suppress("unused")

package org.serverct.parrot.parrotx.function

import taboolib.library.configuration.ConfigurationSection
import java.util.*

fun String?.toUUID(): UUID? = if (this == null) null else runCatching { UUID.fromString(this) }.getOrNull()

inline fun <reified E> Map<*, *>.asList(node: String): List<E>? {
    return when (val obj = this[node]) {
        is E -> listOf(obj)
        is Collection<*> -> obj.filterIsInstance<E>()
        else -> null
    }
}

inline fun <reified E> ConfigurationSection.asList(node: String): List<E>? {
    return when (val obj = this[node]) {
        is E -> listOf(obj)
        is Collection<*> -> obj.filterIsInstance<E>()
        else -> null
    }
}

fun <V> ConfigurationSection.asMap(path: String = "", transfer: ConfigurationSection.(String) -> V?): Map<String, V> {
    val map: MutableMap<String, V> = HashMap()
    (if (path.isBlank()) this else getConfigurationSection(path))?.let { root ->
        root.getKeys(false).forEach { key ->
            map[key] = runCatching {
                root.transfer(key)
            }.onFailure {
                it.printStackTrace()
            }.getOrNull() ?: return@forEach
        }
    }
    return map
}

@JvmName("oneOfIterable")
fun <V> ConfigurationSection.oneOf(paths: Iterable<String>, transfer: ConfigurationSection.(String) -> V?, predicate: (V) -> Boolean = { true }): V? {
    return paths.firstNotNullOfOrNull {
        transfer(this, it)?.takeIf(predicate)
    }
}

@JvmName("oneOfArray")
fun <V> ConfigurationSection.oneOf(paths: Array<out String>, transfer: ConfigurationSection.(String) -> V?, predicate: (V) -> Boolean = { true }): V? {
    return oneOf(setOf(*paths), transfer, predicate)
}

@JvmName("oneOfVararg")
fun <V> ConfigurationSection.oneOf(vararg paths: String, transfer: ConfigurationSection.(String) -> V?, predicate: (V) -> Boolean = { true }): V? {
    return oneOf(setOf(*paths), transfer, predicate)
}