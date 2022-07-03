package org.serverct.parrot.parrotx.function

import taboolib.library.configuration.ConfigurationSection
import java.util.*

fun String?.toUUID(): UUID? = if (this == null) null else runCatching { UUID.fromString(this) }.getOrNull()

inline fun <reified T> Map<*, *>.adaptList(node: String): List<T>? {
    return when (val obj = this[node]) {
        is T -> listOf(obj)
        is Collection<*> -> obj.filterIsInstance<T>()
        else -> null
    }
}

inline fun <reified T> ConfigurationSection.adaptList(node: String): List<T>? {
    return when (val obj = this[node]) {
        is T -> listOf(obj)
        is Collection<*> -> obj.filterIsInstance<T>()
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