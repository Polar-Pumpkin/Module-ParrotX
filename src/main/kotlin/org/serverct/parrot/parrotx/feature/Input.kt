@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package org.serverct.parrot.parrotx.feature

import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.common.util.replaceWithOrder
import taboolib.module.chat.colored
import taboolib.platform.util.sendLang
import java.util.*
import java.util.concurrent.TimeUnit

fun Player.input(name: String, message: String? = null, vararg args: Any, builder: Input.() -> Unit) {
    if (message != null) {
        if (args.isNotEmpty()) {
            sendLang(message, *args)
        } else {
            sendMessage(message.replaceWithOrder(*args).colored())
        }
    }
    Inputs.schedule(this, name, builder)
}

object Inputs {

    private val scheduled: MutableMap<UUID, Input> = HashMap()

    fun schedule(user: Player, name: String, builder: Input.() -> Unit) {
        cancel(user)
        val input = Input(name, builder)
        scheduled[user.uniqueId] = input

        input.launch(user)
    }

    fun cancel(user: Player) = scheduled.remove(user.uniqueId)?.cancel(user)

    fun timeout(user: Player, input: Input) {
        if (!scheduled.remove(user.uniqueId, input)) {
            return
        }
        input.timeout(user)
    }

    @SubscribeEvent(EventPriority.LOWEST, true)
    internal fun onInput(event: AsyncPlayerChatEvent) {
        val user = event.player
        val input = scheduled[user.uniqueId] ?: return
        event.isCancelled = true

        val content = event.message
        when {
            content.startsWith('/') -> input.accept(user, content.substring(1))
            content.startsWith('#') -> input.action(user, content.substring(1))
            else -> input.accept(user, content)
        }
    }

}

class Input(val name: String, builder: Input.() -> Unit) {

    var isCancelled: Boolean = false
        private set
    var isTimeout: Boolean = false
        private set
    var isExecuted: Boolean = false
        private set
    var isAccepted: Boolean = false
        private set

    val isCompleted: Boolean
        get() = isCancelled || isTimeout || isExecuted || isAccepted
    lateinit var input: String
        private set

    private var duration: Long = 0
    private var timeunit: TimeUnit = TimeUnit.MINUTES

    private var onCancel: Input.(Player) -> Unit = {}
    private var onTimeout: Input.(Player) -> Unit = {}
    private var onAction: Input.(Player, String) -> Boolean = { _, _ -> false }
    private var onInput: Input.(Player, String) -> Unit = { _, _ -> }

    init {
        builder()
    }

    fun expired(duration: Long, unit: TimeUnit = TimeUnit.MINUTES) {
        this.duration = duration
        this.timeunit = unit
    }

    fun onCancel(handle: Input.(Player) -> Unit) {
        this.onCancel = handle
    }

    fun onTimeout(handle: Input.(Player) -> Unit) {
        this.onTimeout = handle
    }

    fun onAction(handle: Input.(Player, String) -> Boolean) {
        this.onAction = handle
    }

    fun onInput(handle: Input.(Player, String) -> Unit) {
        this.onInput = handle
    }

    fun launch(user: Player) {
        if (isCompleted || duration <= 0) {
            return
        }

        submit(delay = timeunit.toSeconds(duration) * 20L) {
            if (!user.isOnline) {
                return@submit
            }
            Inputs.timeout(user, this@Input)
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

    fun action(user: Player, action: String) {
        if (isCompleted) {
            return
        }
        if (onAction(user, action)) {
            isExecuted = true
            return
        }
        accept(user, "#$action")
    }

    fun accept(user: Player, content: String) {
        if (isCompleted) {
            return
        }
        isAccepted = true
        input = content
        onInput(user, content)
    }

}

fun Input.onCancel(node: String, vararg args: Any) = onCancel { it.sendLang(node, name, *args) }

fun Input.onTimeout(node: String, vararg args: Any) = onTimeout { it.sendLang(node, name, *args) }