package org.serverct.parrot.parrotx.function

import org.bukkit.OfflinePlayer
import taboolib.platform.util.sendLang

fun OfflinePlayer.trySendLang(node: String, vararg args: Any): Boolean {
    if (isOnline) {
        return false
    }
    player!!.sendLang(node, *args)
    return true
}