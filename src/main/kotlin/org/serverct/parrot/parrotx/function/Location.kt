@file:Isolated

package org.serverct.parrot.parrotx.function

import com.google.gson.JsonObject
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.Isolated

fun Player.teleportTo(to: Location) {
    val ref = location
    teleport(to.clone().apply {
        yaw = ref.yaw
        pitch = ref.pitch
    })
}

fun Location.toJson(): JsonObject {
    return JsonObject().apply {
        addProperty("world", world.name)
        addProperty("x", x)
        addProperty("y", y)
        addProperty("z", z)
    }
}

fun JsonObject.toLocation(): Location {
    fun deserializeError(reason: String): Nothing {
        error("从 Json 反序列化 Location 时$reason")
    }

    val worldname = (get("world") ?: deserializeError("缺少数据: world")).asString
    val world = Bukkit.getWorld(worldname) ?: deserializeError("遇到未知的世界: $worldname")
    val x = (get("x") ?: deserializeError("缺少数据: x")).asDouble
    val y = (get("y") ?: deserializeError("缺少数据: y")).asDouble
    val z = (get("z") ?: deserializeError("缺少数据: z")).asDouble

    return Location(world, x, y, z)
}