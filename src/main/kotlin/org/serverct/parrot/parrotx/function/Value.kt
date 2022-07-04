package org.serverct.parrot.parrotx.function

import org.bukkit.Bukkit
import java.util.*
import java.util.concurrent.TimeUnit

fun usernameOrNull(uniqueId: UUID): String? {
    return Bukkit.getOfflinePlayer(uniqueId)?.name
}

fun username(uniqueId: UUID): String {
    return usernameOrNull(uniqueId) ?: "$uniqueId"
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
    val hour = TimeUnit.HOURS.toSeconds(1)
    val minute = TimeUnit.MINUTES.toSeconds(1)

    val builder = StringBuilder()
    (left / hour).also { hours ->
        if (hours > 0) {
            builder.append("$hours ${units[TimeUnit.HOURS] ?: "hr(s)"}")
            left %= hour
        }
    }
    (left / minute).also { minutes ->
        if (minutes > 0) {
            if (builder.isNotEmpty()) {
                builder.append(" ")
            }
            builder.append("$minute ${units[TimeUnit.MINUTES] ?: "min(s)"}")
            left %= minute
        }
    }
    if (left > 0) {
        if (builder.isNotEmpty()) {
            builder.append(" ")
        }
        builder.append("$left ${units[TimeUnit.SECONDS] ?: "sec(s)"}")
    }
    return builder.toString()
}