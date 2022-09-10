@file:Suppress("unused")

package org.serverct.parrot.parrotx.function

import org.bukkit.Location

fun Location.center(): Location {
    return clone().apply {
        x = blockX + 0.5
        y = blockY + 1.2
        z = blockZ + 0.5
    }
}

fun Location.format(pattern: String = "{x}, {y}, {z} ({world})"): String {
    return VariableReaders.BRACES.replaceNested(pattern) {
        when (this) {
            "x" -> "$blockX"
            "y" -> "$blockY"
            "z" -> "$blockZ"
            "world" -> world?.name ?: "未知世界"
            else -> ""
        }
    }
}