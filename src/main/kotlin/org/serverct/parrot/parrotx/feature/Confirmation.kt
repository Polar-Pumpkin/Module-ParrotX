@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package org.serverct.parrot.parrotx.feature

import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.module.chat.colored
import taboolib.platform.util.sendLang
import java.util.*
import java.util.concurrent.TimeUnit

fun Player.confirm(name: String, message: String? = null, vararg args: Any, builder: Confirmation.() -> Unit) {
    if (message != null) {
        if (args.isNotEmpty()) {
            sendLang(message, *args)
        } else {
            sendMessage(message.colored())
        }
    }
    Confirmations.schedule(this, name, builder)
}

object Confirmations {

    private val scheduled: MutableMap<UUID, Confirmation> = HashMap()

    fun schedule(user: Player, name: String, builder: Confirmation.() -> Unit) {
        cancel(user)
        val confirmation = Confirmation(name, builder)
        scheduled[user.uniqueId] = confirmation

        confirmation.launch(user)
    }

    fun cancel(user: Player) = scheduled.remove(user.uniqueId)?.cancel(user)

    fun timeout(user: Player, confirmation: Confirmation) {
        if (!scheduled.remove(user.uniqueId, confirmation)) {
            return
        }
        confirmation.timeout(user)
    }

    @SubscribeEvent(EventPriority.LOWEST, true)
    internal fun onQuit(event: PlayerQuitEvent) {
        scheduled.remove(event.player.uniqueId)
    }

}

class Confirmation(val name: String, builder: Confirmation.() -> Unit) {

    var isCancelled: Boolean = false
        private set
    var isTimeout: Boolean = false
        private set
    var isConfirmed: Boolean = false
        private set

    val isCompleted: Boolean
        get() = isCancelled || isTimeout || isConfirmed

    private var duration: Long = 0
    private var timeunit: TimeUnit = TimeUnit.MINUTES

    private var onCancel: Confirmation.(Player) -> Unit = {}
    private var onTimeout: Confirmation.(Player) -> Unit = {}
    private var onConfirm: Confirmation.(Player) -> Unit = {}
    private var onException: Confirmation.(Player, Throwable) -> Unit = { _, _ -> }

    init {
        builder()
    }

    fun expired(duration: Long, unit: TimeUnit = TimeUnit.MINUTES) {
        this.duration = duration
        this.timeunit = unit
    }

    fun onCancel(handle: Confirmation.(Player) -> Unit) {
        this.onCancel = handle
    }

    fun onTimeout(handle: Confirmation.(Player) -> Unit) {
        this.onTimeout = handle
    }

    fun onConfirm(handle: Confirmation.(Player) -> Unit) {
        this.onConfirm = handle
    }

    fun onException(handle: Confirmation.(Player, Throwable) -> Unit) {
        this.onException = handle
    }

    fun launch(user: Player) {
        if (isCompleted || duration <= 0) {
            return
        }

        submit(delay = timeunit.toSeconds(duration) * 20L) {
            if (!user.isOnline) {
                return@submit
            }
            Confirmations.timeout(user, this@Confirmation)
        }
    }

    fun cancel(user: Player) {
        if (isCompleted) {
            return
        }
        isCancelled = true
        onCancel(user)
    }

    fun timeout(user: Player) {
        if (isCompleted) {
            return
        }
        isTimeout = true
        onTimeout(user)
    }

    fun confirm(user: Player) {
        if (isCompleted) {
            return
        }
        isConfirmed = true
        try {
            onConfirm(user)
        } catch (ex: Throwable) {
            onException(user, ex)
        }
    }

}

fun Confirmation.onCancel(node: String, vararg args: Any) = onCancel { it.sendLang(node, name, *args) }

fun Confirmation.onTimeout(node: String, vararg args: Any) = onTimeout { it.sendLang(node, name, *args) }