package org.serverct.parrot.parrotx.util

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mojang.authlib.GameProfile
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import org.serverct.parrot.parrotx.function.textured
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.warning
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.invokeMethod
import taboolib.library.xseries.XMaterial
import java.net.URL
import java.util.concurrent.CompletableFuture

/**
 * 参考 TrMenu 的 Heads 工具
 *
 * @author Arasple, legoshi
 * @version 1
 * @since 2023-07-22 21:16
 */
@Suppress("DEPRECATION", "MemberVisibilityCanBePrivate", "unused")
object Heads {

    private val emptyHead = XMaterial.PLAYER_HEAD.parseItem()!!
    private val cachedTextures = mutableMapOf<String, String?>()
    private val cachedSkulls = mutableMapOf<String, ItemStack>()
    private val mojangApi = arrayOf(
        "https://api.mojang.com/users/profiles/minecraft/",
        "https://sessionserver.mojang.com/session/minecraft/profile/"
    )

    fun getHead(value: String): ItemStack {
        return if (value.length > 25) emptyHead.clone().textured(value) else getPlayerHead(value)
    }

    fun getPlayerHead(username: String): ItemStack {
        return cachedSkulls.computeIfAbsent(username) {
            emptyHead.clone().also {
                getPlayerTexture(username).thenAccept { texture ->
                    if (texture != null) {
                        it textured texture
                    }
                }
            }
        }.clone()
    }

    fun getPlayerTexture(username: String): CompletableFuture<String?> {
        // from cache
        cachedTextures[username]?.let { return CompletableFuture.completedFuture(it) }
        // from Player
        Bukkit.getPlayerExact(username)?.invokeMethod<GameProfile>("getProfile")
            ?.let { fromGameProfile(username, it) }
            ?.let { return it }
        // from OfflinePlayer
        Bukkit.getOfflinePlayer(username).getProperty<GameProfile>("profile")
            ?.let { fromGameProfile(username, it) }
            ?.let { return it }
        // from Mojang API
        val future = CompletableFuture<String?>()
        submit(async = true) {
            val parser = JsonParser()
            val mapping = parser.parse(fromUrl("${mojangApi[0]}$username")) as? JsonObject
                ?: return@submit warning("Player $username's uuid not found")
            val uniqueId = mapping["id"].asString
            val profile = parser.parse(fromUrl("${mojangApi[1]}$uniqueId")) as JsonObject
            profile.getAsJsonArray("properties")
                .map { it.asJsonObject }
                .firstOrNull { "textures" == it["name"].asString }
                ?.let { it["value"].asString }
                ?.let {
                    cachedTextures[username] = it
                    future.complete(it)
                } ?: future.complete(null)
        }
        return future
    }

    private fun fromGameProfile(username: String, profile: GameProfile): CompletableFuture<String?>? {
        profile.properties
            ?.get("textures")
            ?.find { it.value != null }
            ?.value
            ?.let {
                cachedTextures[username] = it
                return CompletableFuture.completedFuture(it)
            }
        return null
    }

    private fun fromUrl(url: String): String {
        return try {
            String(URL(url).openStream().readBytes())
        } catch (_: Throwable) {
            ""
        }
    }

}