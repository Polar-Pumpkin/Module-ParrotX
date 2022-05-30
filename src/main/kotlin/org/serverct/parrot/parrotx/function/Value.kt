package org.serverct.parrot.parrotx.function

import org.bukkit.Bukkit
import java.util.*

fun username(uniqueId: UUID): String {
    return Bukkit.getPlayer(uniqueId)?.name ?: Bukkit.getOfflinePlayer(uniqueId)?.name ?: "$uniqueId"
}