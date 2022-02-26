package org.serverct.parrot.parrotx.search.location

import org.bukkit.Location
import org.bukkit.entity.Entity
import org.serverct.parrot.parrotx.search.api.provider.Provider
import taboolib.common.platform.function.submit
import java.util.*
import java.util.concurrent.CompletableFuture


@Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")
class SquareDiffusion(val center: Location) : Provider<Location> {

    constructor(entity: Entity) : this(entity.location)

    val world = center.world
    val centerX = center.blockX
    val centerY = center.blockY
    val centerZ = center.blockZ

    var radius: Int = 0
        private set

    override fun generate(): CompletableFuture<Queue<Location>> {
        radius++
        val future = CompletableFuture<Queue<Location>>()
        submit(async = true) {
            val queue = LinkedList<Location>()

            val y = centerY.toDouble()
            val west = centerX - radius
            val east = centerX + radius
            val north = centerZ - radius
            val south = centerZ + radius

            for (z in north..south) {
                queue.offer(Location(world, west.toDouble(), y, z.toDouble()))
                queue.offer(Location(world, east.toDouble(), y, z.toDouble()))
            }

            for (x in (west + 1) until east) {
                queue.offer(Location(world, x.toDouble(), y, north.toDouble()))
                queue.offer(Location(world, x.toDouble(), y, south.toDouble()))
            }

            future.complete(queue)
        }
        return future
    }

}