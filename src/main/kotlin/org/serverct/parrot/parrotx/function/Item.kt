@file:Suppress("unused")

package org.serverct.parrot.parrotx.function

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import taboolib.common.platform.function.pluginId
import taboolib.common.util.VariableReader
import taboolib.library.reflex.Reflex.Companion.setProperty
import taboolib.module.chat.colored
import taboolib.platform.util.modifyMeta
import java.util.*

fun ItemStack.variables(reader: VariableReader = VariableReaders.BRACES, func: VariableFunction): ItemStack {
    return modifyMeta<ItemMeta> {
        displayName = displayName?.let {
            reader.replaceNested(it) { func.transfer(this)?.firstOrNull() ?: this }.colored()
        }
        lore = lore?.variables(reader, func)?.colored()
    }
}

fun ItemStack.transform(reader: VariableReader = VariableReaders.BRACES, builder: VariableTransformerBuilder.() -> Unit): ItemStack {
    return variables(reader, VariableTransformerBuilder(builder))
}

fun ItemStack.variable(key: String, value: Collection<String>, reader: VariableReader = VariableReaders.BRACES): ItemStack {
    return variables(reader) { if (it == key) value else null }
}

fun ItemStack.singletons(reader: VariableReader = VariableReaders.BRACES, func: SingleVariableFunction): ItemStack {
    return variables(reader, func)
}

fun ItemStack.singleton(key: String, value: String, reader: VariableReader = VariableReaders.BRACES): ItemStack {
    return singletons(reader) { if (it == key) value else null }
}

fun ItemStack.areas(filter: AreaFilter): ItemStack {
    return modifyMeta<ItemMeta> {
        lore = lore?.areas(filter)?.colored()
    }
}

fun ItemStack.areas(builder: AreaFilterBuilder.() -> Unit): ItemStack {
    return areas(AreaFilterBuilder(builder))
}

@Suppress("HttpUrlsUsage")
var headUrl: String = "http://textures.minecraft.net/texture/"

infix fun ItemStack.textured(input: String): ItemStack {
    fun encodeTexture(input: String): String {
        return with(Base64.getEncoder()) {
            encodeToString("{\"textures\":{\"SKIN\":{\"url\":\"$headUrl$input\"}}}".toByteArray())
        }
    }

    return modifyMeta<SkullMeta> {
        val profile = GameProfile(UUID.randomUUID(), null)
        val texture = if (input.length in 60..100) encodeTexture(input) else input
        profile.properties.put("textures", Property("textures", texture, "${pluginId}_TexturedSkull"))

        setProperty("profile", profile)
    }
}