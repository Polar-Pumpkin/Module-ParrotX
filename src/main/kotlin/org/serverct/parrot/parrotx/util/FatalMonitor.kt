@file:Isolated

package org.serverct.parrot.parrotx.util

import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerLoginEvent
import taboolib.common.Isolated
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.pluginId

var hasFatalError: Boolean = false

inline fun <R> fatalAction(action: () -> R): Result<R> {
    return runCatching {
        action()
    }.onFailure {
        Bukkit.getOnlinePlayers().forEach { online ->
            online.kickPlayer("[${pluginId}] Fatal error")
        }
        hasFatalError = true
    }
}

@Isolated
internal object LoginListener {
    @SubscribeEvent
    fun onLogin(event: PlayerLoginEvent) {
        if (hasFatalError) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "[${pluginId}] Fatal error")
        }
    }
}