@file:Suppress("unused")

package org.serverct.parrot.parrotx.feature

import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerLoginEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.pluginId
import taboolib.common.platform.function.submit
import java.util.concurrent.atomic.AtomicBoolean

val hasFatalError: AtomicBoolean = AtomicBoolean(false)

inline fun <R> important(action: () -> R): Result<R> {
    return runCatching {
        action()
    }.onFailure { _ ->
        submit {
            Bukkit.getOnlinePlayers().forEach {
                it.kickPlayer("[${pluginId}] Fatal error")
            }
            hasFatalError.set(true)
        }
    }
}

internal object LoginListener {
    @SubscribeEvent(EventPriority.LOWEST, true)
    fun onLogin(event: PlayerLoginEvent) {
        if (hasFatalError.get()) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "[${pluginId}] Fatal error")
        }
    }
}