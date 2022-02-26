package org.serverct.parrot.parrotx.search.location

import org.bukkit.Location
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
        return at.block?.let { it.type.isSolid && it.getRelative(0, 1, 0).isEmpty && it.getRelative(0, 2, 0).isEmpty } ?: false
    }

}