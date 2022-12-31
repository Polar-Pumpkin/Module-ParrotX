package org.serverct.parrot.parrotx.ui.config.advance

import com.google.common.collect.HashMultimap
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.serverct.parrot.parrotx.container.SimpleRegistry
import org.serverct.parrot.parrotx.function.asMap
import org.serverct.parrot.parrotx.function.oneOf
import org.serverct.parrot.parrotx.ui.MenuItem
import org.serverct.parrot.parrotx.ui.config.MenuConfiguration
import org.serverct.parrot.parrotx.ui.config.MenuPart
import taboolib.library.configuration.ConfigurationSection

@Suppress("MemberVisibilityCanBePrivate", "unused")
class TemplateConfiguration(val holder: MenuConfiguration) : SimpleRegistry<Char, MenuItem>(HashMap()) {

    override fun getKey(value: MenuItem): Char = value.char

    init {
        holder.source.oneOf(*MenuPart.TEMPLATE.paths, transfer = ConfigurationSection::getConfigurationSection)
            ?.asMap { getConfigurationSection(it) }
            ?.forEach { (key, section) ->
                register {
                    MenuItem(holder, section)
                }.onFailure {
                    MenuPart.TEMPLATE incorrect ("加载 $key 时遇到错误" to it)
                }
            }
        registered.putIfAbsent(' ', MenuItem(holder, ' ', HashMultimap.create(), ItemStack(Material.AIR)))
    }

    operator fun get(slot: Int): MenuItem? = get(holder.shape[slot])

    operator fun get(keyword: String): MenuItem? = get(holder.keywords[keyword])

    fun require(char: Char): MenuItem = get(char) ?: (MenuPart.TEMPLATE incorrect "未配置字符 $char 对应的模板")

    fun require(slot: Int): MenuItem = get(slot) ?: (MenuPart.TEMPLATE incorrect "未配置字符 ${holder.shape[slot]}@$slot 对应的模板")

    fun require(keyword: String): MenuItem = get(keyword)!!

    operator fun invoke(keyword: String, slot: Int, index: Int, isFallback: Boolean = false, fallback: String = "Fallback", vararg args: Any?): ItemStack {
        return if (isFallback) {
            get(fallback)?.invoke(slot, index, *args) ?: ItemStack(Material.AIR)
        } else {
            require(keyword).invoke(slot, index, *args)
        }
    }

}