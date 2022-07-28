@file:Isolated
@file:Suppress("unused")

package org.serverct.parrot.parrotx.function

import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import taboolib.common.Isolated
import taboolib.platform.util.sendInfoMessage
import taboolib.platform.util.sendLang

fun OfflinePlayer.trySendLang(node: String, vararg args: Any): Boolean {
    if (!isOnline) {
        return false
    }
    return runCatching {
        player!!.sendLang(node, *args)
        true
    }.getOrElse { false }
}

fun Player.debug(message: String, vararg args: Any) {
    if (isOp) {
        sendInfoMessage(message, *args)
    }
}