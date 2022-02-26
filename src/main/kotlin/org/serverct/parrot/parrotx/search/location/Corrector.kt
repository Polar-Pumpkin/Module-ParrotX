package org.serverct.parrot.parrotx.search.location

import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.serverct.parrot.parrotx.search.api.corrector.Corrector
import taboolib.common.platform.function.submit
import java.util.concurrent.CompletableFuture

object SurfaceCorrector : Corrector<Location> {

    private val heights = 0 until 255

    override fun correct(value: Location): CompletableFuture<Location> {
        val future = CompletableFuture<Location>()
        if (value.world == null || value.blockY < 0) {
            future.completeExceptionally(IllegalArgumentException("Invalid location: $value"))
            return future
        }
        submit(async = true) {
            var block = value.block
            while (block.y in heights && !isSurface(block)) {
                block = closer(block)
            }
            future.complete(block.location)
        }
        return future
    }

    private fun isSurface(block: Block): Boolean = !block.isEmpty && block.getRelative(BlockFace.UP).isEmpty

    private fun closer(block: Block): Block = when {
        block.isEmpty -> block.getRelative(BlockFace.DOWN)
        block.isLiquid -> block.getRelative(BlockFace.UP)
        else -> block
    }

}