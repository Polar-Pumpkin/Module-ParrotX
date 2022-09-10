@file:Isolated
@file:Suppress("unused")

package org.serverct.parrot.parrotx.feature

import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerLoginEvent
import taboolib.common.Isolated
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.pluginId

var hasFatalError: Boolean = false

inline fun <R> important(action: () -> R): Result<R> {
    return runCatching {
        action()
    }.onFailure { _ ->
        Bukkit.getOnlinePlayers().forEach {
            it.kickPlayer("[${pluginId}] Fatal error")
        }
        hasFatalError = true
    }
}

@Isolated
internal object LoginListener {
    @SubscribeEvent(EventPriority.LOWEST, true)
    fun onLogin(event: PlayerLoginEvent) {
        if (hasFatalError) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "[${pluginId}] Fatal error")
        }
    }
}