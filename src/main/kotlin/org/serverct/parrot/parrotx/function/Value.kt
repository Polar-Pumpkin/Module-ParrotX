@file:Isolated
@file:Suppress("unused")

package org.serverct.parrot.parrotx.function

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import taboolib.common.Isolated
import java.util.*
import java.util.concurrent.TimeUnit

fun usernameOrNull(uniqueId: UUID): String? {
    return Bukkit.getOfflinePlayer(uniqueId)?.name
}

fun username(uniqueId: UUID): String {
    return usernameOrNull(uniqueId) ?: "$uniqueId"
}

fun user(user: OfflinePlayer, fallbackName: String? = "null"): String {
    return "${user.name ?: fallbackName ?: "null"}(${user.uniqueId})"
}

fun duration(
    seconds: Long,
    units: Map<TimeUnit, String> = mapOf(
        TimeUnit.HOURS to "小时",
        TimeUnit.MINUTES to "分钟",
        TimeUnit.SECONDS to "秒"
    )
): String {
    var left = seconds
    val builder = StringBuilder()
    units.forEach { (unit, name) ->
        val duration = unit.toSeconds(1)
        val value = left / duration
        if (value < 0) {
            return@forEach
        }
        if (builder.isNotEmpty()) {
            builder.append(" ")
        }
        builder.append("$value $name")
        left %= duration
    }
    return builder.toString().trim()
}