package org.serverct.parrot.parrotx.search.location

import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.serverct.parrot.parrotx.search.api.corrector.Corrector
import taboolib.common.platform.function.submit
import java.util.concurrent.CompletableFuture

object SurfaceCorrector : Corrector<Location> {

    override fun correct(value: Location): CompletableFuture<Location> {
        val future = CompletableFuture<Location>()
        if (value.world == null || value.blockY < 0) {
            future.completeExceptionally(IllegalArgumentException("Invalid location: $value"))
            return future
        }

        submit(async = true) {
            var block = value.block
            while (block.y > 0 && block.isEmpty) {
                block = block.getRelative(BlockFace.DOWN)
            }
            future.complete(block.location)
        }

        return future
    }

}