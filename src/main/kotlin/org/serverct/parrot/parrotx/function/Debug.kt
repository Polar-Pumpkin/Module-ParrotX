package org.serverct.parrot.parrotx.function

import org.bukkit.entity.Player
import taboolib.platform.util.sendInfoMessage

fun Player.debug(message: String, vararg args: Any) {
    if (isOp) {
        sendInfoMessage(message, *args)
    }
}