package org.serverct.parrot.parrotx.search.location

import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.serverct.parrot.parrotx.search.api.filter.Filter
import java.util.concurrent.CompletableFuture

@Suppress("MemberVisibilityCanBePrivate")
object StandableFilter : Filter<Location> {

    override fun predict(value: Location): CompletableFuture<Boolean> {
        return CompletableFuture.completedFuture(value.y >= 0 && standable(value))
    }

    fun standable(at: Location): Boolean {
        if (at.world == null) {
            return false
        }
        val block = at.block ?: return false
        return if (block.type.isSolid) {
            block.getRelative(BlockFace.UP).isEmpty && block.getRelative(BlockFace.UP, 2).isEmpty
        } else {
            !block.isEmpty && !block.isLiquid && block.getRelative(BlockFace.DOWN).type.isSolid && block.getRelative(BlockFace.UP).isEmpty
        }
    }

}