package org.serverct.parrot.parrotx.util

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerToggleFlightEvent
import taboolib.common.Isolated
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Suppress("MemberVisibilityCanBePrivate")
@Isolated
object PlayerFreezer {

    private val labels = ConcurrentHashMap<UUID, FrozenLabel>()

    fun Player.freeze() {
        labels[uniqueId] = FrozenLabel(walkSpeed, flySpeed, allowFlight, isFlying)
        isInvulnerable = true
        walkSpeed = 0.0F
        flySpeed = 0.0F
        allowFlight = true
        isFlying = true
    }

    fun release(uniqueId: UUID) {
        val (walkSpeed, flySpeed, allowFlight, isFlying) = labels.remove(uniqueId) ?: return
        Bukkit.getPlayer(uniqueId)?.let {
            it.isInvulnerable = false
            it.walkSpeed = walkSpeed
            it.flySpeed = flySpeed
            it.allowFlight = allowFlight
            it.isFlying = isFlying
        }
    }

    fun releaseAll() {
        labels.keys.forEach { release(it) }
    }

    @SubscribeEvent
    internal fun onToggleFlight(event: PlayerToggleFlightEvent) {
        val uniqueId = event.player.uniqueId
        if (labels.containsKey(uniqueId)) {
            event.isCancelled = true
        }
    }

    @SubscribeEvent
    internal fun onJoin(event: PlayerJoinEvent) {
        var modified = false
        with(event.player) {
            if (isInvulnerable) {
                isInvulnerable = false
                modified = true
            }

            if (walkSpeed == 0.0F) {
                walkSpeed = 0.2F
                modified = true
            }

            if (flySpeed == 0.0F) {
                flySpeed = 0.1F
                modified = true
            }

            if (modified) {
                info("Release unexpected frozen player: $name")
            }
        }
    }

    @SubscribeEvent
    internal fun onQuit(event: PlayerQuitEvent) {
        val uniqueId = event.player.uniqueId
        release(uniqueId)
    }

    @Awake(LifeCycle.DISABLE)
    internal fun onDisable() {
        releaseAll()
    }

    data class FrozenLabel(val walkSpeed: Float, val flySpeed: Float, val allowFlight: Boolean, val isFlying: Boolean)

}