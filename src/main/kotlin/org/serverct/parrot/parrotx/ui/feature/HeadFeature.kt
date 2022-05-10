package org.serverct.parrot.parrotx.ui.feature

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.serverct.parrot.parrotx.function.getData
import org.serverct.parrot.parrotx.ui.config.MenuConfiguration
import org.serverct.parrot.parrotx.ui.MenuFeature
import taboolib.common.reflect.Reflex.Companion.setProperty
import taboolib.library.xseries.XMaterial
import java.util.*

object HeadFeature : MenuFeature() {

    override val name: String = "Head"

    override fun buildIcon(config: MenuConfiguration, data: Map<*, *>, icon: ItemStack, vararg args: Any?): ItemStack {
        if (!XMaterial.PLAYER_HEAD.isSimilar(icon)) {
            return icon
        }
        return modifyTexture(data.getData("Texture"), icon)
    }

    private fun modifyTexture(input: String, itemStack: ItemStack): ItemStack {
        val meta = itemStack.itemMeta as SkullMeta
        val profile = GameProfile(UUID.randomUUID(), null)
        val texture = if (input.length in 60..100) encodeTexture(input) else input

        profile.properties.put("textures", Property("textures", texture, "OxygenFriend_TexturedSkull"))
        meta.setProperty("profile", profile)
        itemStack.itemMeta = meta
        return itemStack
    }

    private fun encodeTexture(input: String): String {
        val encoder = Base64.getEncoder()
        return encoder.encodeToString("{\"textures\":{\"SKIN\":{\"url\":\"http://textures.minecraft.net/texture/$input\"}}}".toByteArray())
    }

}