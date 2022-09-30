@file:Isolated
@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package org.serverct.parrot.parrotx.feature

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.EquipmentSlot
import org.serverct.parrot.parrotx.function.format
import taboolib.common.Isolated
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.chat.colored
import taboolib.platform.util.sendLang
import java.util.*

fun Player.select(name: String, message: String? = null, vararg args: Any, builder: Selection.() -> Unit) {
    if (message != null) {
        if (args.isNotEmpty()) {
            sendLang(message, *args)
        } else {
            sendMessage(message.colored())
        }
    }
    Selections.schedule(this, name, builder)
}

@Isolated
object Selections {

    private val scheduled: MutableMap<UUID, Selection> = HashMap()

    fun schedule(user: Player, name: String, builder: Selection.() -> Unit) {
        cancel(user)
        scheduled[user.uniqueId] = Selection(name, builder)
    }

    fun cancel(user: Player): Boolean = scheduled.remove(user.uniqueId)?.cancel(user) == Unit

    fun confirm(user: Player): Boolean? {
        val selection = scheduled[user.uniqueId] ?: return null
        return selection.complete(user, true)
    }

    internal fun unregister(uniqueId: UUID, selection: Selection): Boolean = scheduled.remove(uniqueId, selection)

    @SubscribeEvent(EventPriority.LOWEST, true)
    internal fun onSelect(event: PlayerInteractEvent) {
        if (event.hand != EquipmentSlot.HAND) {
            return
        }

        val user = event.player
        val loc = event.clickedBlock?.location ?: return
        val selection = scheduled[user.uniqueId] ?: return
        when (event.action) {
            Action.LEFT_CLICK_BLOCK -> selection.selectA(user, loc)
            Action.RIGHT_CLICK_BLOCK -> selection.selectB(user, loc)
            else -> return
        }
        event.isCancelled = true
    }

    @SubscribeEvent(EventPriority.LOWEST, true)
    internal fun onQuit(event: PlayerQuitEvent) {
        scheduled.remove(event.player.uniqueId)
    }

}

@Isolated
class Selection(val name: String, builder: Selection.() -> Unit) {

    var isCompleted: Boolean = false
        private set

    var locA: Location? = null
    var locB: Location? = null

    private var onSelectA: Selection.(Player, Location) -> Location? = { _, it -> it }
    private var onSelectB: Selection.(Player, Location) -> Location? = { _, it -> it }
    private var onCancel: Selection.(Player) -> Unit = {}
    private var onComplete: Selection.(Player, Location, Location) -> Unit = { _, _, _ -> }
    private var onConfirm: Selection.(Player, Location, Location) -> Unit = { _, _, _ -> }

    init {
        builder()
    }

    fun onSelectA(handle: Selection.(Player, Location) -> Location?) {
        this.onSelectA = handle
    }

    fun onSelectB(handle: Selection.(Player, Location) -> Location?) {
        this.onSelectB = handle
    }

    fun onCancel(handle: Selection.(Player) -> Unit) {
        this.onCancel = handle
    }

    fun onComplete(handle: Selection.(Player, Location, Location) -> Unit) {
        this.onComplete = handle
    }

    fun onConfirm(handle: Selection.(Player, Location, Location) -> Unit) {
        this.onConfirm = handle
    }

    fun selectA(user: Player, loc: Location) {
        if (isCompleted) {
            return
        }
        this.locA = onSelectA(user, loc)
        complete(user)
    }

    fun selectB(user: Player, loc: Location) {
        if (isCompleted) {
            return
        }
        this.locB = onSelectB(user, loc)
        complete(user)
    }

    fun cancel(user: Player) {
        if (isCompleted) {
            return
        }
        onCancel(user)
    }

    fun complete(user: Player, confirmed: Boolean = false): Boolean {
        if (isCompleted) {
            return false
        }
        val locA = this.locA
        val locB = this.locB
        if (locA == null || locB == null) {
            return false
        }
        if (locA.world == null || locB.world == null) {
            return false
        }
        if (locA.world != locB.world) {
            return false
        }

        when {
            locA.blockX != locB.blockX -> (if (locA.blockX >= locB.blockX) locA else locB).add(1.0, 0.0, 0.0)
            locA.blockY != locB.blockY -> (if (locA.blockY >= locB.blockY) locA else locB).add(0.0, 1.0, 0.0)
            locA.blockZ != locB.blockZ -> (if (locA.blockZ >= locB.blockZ) locA else locB).add(0.0, 0.0, 1.0)
        }

        // val world = locA.world
        // val vectorA = locA.toVector()
        // val vectorB = locB.toVector()
        // val min = Vector.getMinimum(vectorA, vectorB).toLocation(world)
        // val max = Vector.getMaximum(vectorA, vectorB).toLocation(world)
        if (confirmed) {
            Selections.unregister(user.uniqueId, this)
            onConfirm(user, locA, locB)
        } else {
            onComplete(user, locA, locB)
        }
        return true
    }

}

fun Selection.onSelectA(node: String, vararg args: Any) = onSelectA { user, it -> it.also { user.sendLang(node, name, it.format(), *args) } }

fun Selection.onSelectB(node: String, vararg args: Any) = onSelectB { user, it -> it.also { user.sendLang(node, name, it.format(), *args) } }