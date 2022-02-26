package org.serverct.parrot.parrotx.util

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.Isolated
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Suppress("MemberVisibilityCanBePrivate")
@Isolated
object PlayerFreezer {

    private val labels = ConcurrentHashMap<UUID, FrozenLabel>()

    fun Player.freeze() {
        labels[uniqueId] = FrozenLabel(walkSpeed, flySpeed, allowFlight, isFlying)
        walkSpeed = 0.0F
        flySpeed = 0.0F
        allowFlight = true
        isFlying = true
    }

    fun release(uniqueId: UUID) {
        val (walkSpeed, flySpeed, allowFlight, isFlying) = labels[uniqueId] ?: return
        Bukkit.getPlayer(uniqueId)?.let {
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
    internal fun onQuit(event: PlayerQuitEvent) {
        val uniqueId = event.player.uniqueId
        release(uniqueId)
        labels.remove(uniqueId)
    }

    @Awake(LifeCycle.DISABLE)
    internal fun onDisable() {
        releaseAll()
    }

    data class FrozenLabel(val walkSpeed: Float, val flySpeed: Float, val allowFlight: Boolean, val isFlying: Boolean)

}