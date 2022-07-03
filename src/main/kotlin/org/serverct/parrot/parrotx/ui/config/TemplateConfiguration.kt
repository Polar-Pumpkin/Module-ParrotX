package org.serverct.parrot.parrotx.ui.config

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.serverct.parrot.parrotx.container.SimpleRegistry
import org.serverct.parrot.parrotx.function.asMap
import org.serverct.parrot.parrotx.ui.MenuItem

@Suppress("MemberVisibilityCanBePrivate")
class TemplateConfiguration(val holder: MenuConfiguration) : SimpleRegistry<Char, MenuItem>() {

    override val registered: MutableMap<Char, MenuItem> = HashMap()
    override val MenuItem.key: Char
        get() = char

    init {
        holder.source.asMap(Option.TEMPLATE.path) { getConfigurationSection(it) }
            .forEach { (key, section) ->
                register {
                    MenuItem(holder, section)
                }.onFailure {
                    Option.TEMPLATE.exception("加载 $key 时遇到错误", it).printStackTrace()
                }
            }
        registered.putIfAbsent(' ', MenuItem(holder, ' ', ItemStack(Material.AIR), ArrayList()))
    }

    operator fun get(slot: Int): MenuItem? = get(holder.shape[slot])

    operator fun get(keyword: String): MenuItem? = get(holder.keywords[keyword])

    fun require(char: Char): MenuItem = get(char) ?: Option.TEMPLATE.incorrect("未配置字符 $char 对应项目")

    fun require(slot: Int): MenuItem = get(slot) ?: Option.TEMPLATE.incorrect("未配置字符 ${holder.shape[slot]}@$slot 对应项目")

    fun require(keyword: String): MenuItem = get(keyword)!!

    operator fun invoke(keyword: String, isFallback: Boolean = false, fallback: String = "Fallback", vararg args: Any?): ItemStack {
        return if (isFallback) {
            get(fallback)?.invoke(*args) ?: ItemStack(Material.AIR)
        } else {
            require(keyword).invoke(*args)
        }
    }

}