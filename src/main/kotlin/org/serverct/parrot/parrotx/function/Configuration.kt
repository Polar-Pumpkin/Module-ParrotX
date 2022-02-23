package org.serverct.parrot.parrotx.function

import taboolib.library.configuration.ConfigurationSection
import java.util.*

fun String?.toUUID(): UUID? = if (this == null) null else runCatching { UUID.fromString(this) }.getOrNull()

inline fun <reified T> Map<*, *>.getAdaptedList(node: String): List<T>? {
    return when (val obj = this[node]) {
        is T -> listOf(obj)
        is List<*> -> obj.filterIsInstance<T>()
        else -> null
    }
}

inline fun <reified T> ConfigurationSection.getAdaptedList(node: String): List<T>? {
    return when (val obj = this[node]) {
        is T -> listOf(obj)
        is List<*> -> obj.filterIsInstance<T>()
        else -> null
    }
}

fun <V> ConfigurationSection.mapAs(
    path: String = "",
    transfer: ConfigurationSection.(String) -> V?
): MutableMap<String, V> {
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