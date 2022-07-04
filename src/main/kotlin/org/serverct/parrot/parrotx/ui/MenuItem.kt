package org.serverct.parrot.parrotx.ui

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.serverct.parrot.parrotx.ui.config.MenuConfiguration
import taboolib.common.platform.function.info
import taboolib.common.platform.function.warning
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.XItemStack
import taboolib.module.chat.colored
import taboolib.module.ui.ClickEvent
import taboolib.platform.util.isAir
import taboolib.platform.util.modifyMeta

@Suppress("MemberVisibilityCanBePrivate")
class MenuItem(
    val config: MenuConfiguration,
    val char: Char,
    private val internalIcon: ItemStack,
    val features: List<MappedMenuFeature>
) : ParsedMenuFeature() {

    constructor(config: MenuConfiguration, section: ConfigurationSection) : this(
        config,
        section.name.firstOrNull() ?: error("无法获取菜单图标映射的字符"),
        if ("${section["material"]}".uppercase() == "AIR"){
            ItemStack(Material.AIR)
        } else {
            try {
                XItemStack.deserialize(section).let { item ->
                    if (item.isAir()) {
                        return@let item
                    }
                    item.modifyMeta<ItemMeta> {
                        displayName = displayName?.colored()
                        lore = lore?.map { it.colored() }
                    }
                }
            } catch (ex: Throwable) {
                throw IllegalStateException("无法获取菜单图标的展示物品", ex)
            }
        },
        MenuFeature.mapAll(config, section.getMapList("feature"))
    )

    val icon: ItemStack
        get() = internalIcon.clone()

    override fun buildIcon(icon: ItemStack, vararg args: Any?): ItemStack {
        debug("为菜单图标 $char 构建显示物品:")
        var result = icon.clone()
        features.forEach {
            if (!it.isMapped) {
                warning("菜单图标 $char 配置了一项无法识别的 MenuFeature: ${it.reason}")
                return@forEach
            }
            result = it.buildIcon(result, *args)
            debug("  - ${it.executor!!::class.simpleName}: ${it.data}")
        }
        return result
    }

    operator fun invoke(vararg args: Any?): ItemStack = buildIcon(icon, *args)

    override fun handle(event: ClickEvent, vararg args: Any?) {
        debug("处理菜单图标 $char 的点击事件:")
        if (features.isEmpty()) {
            event.isCancelled = true
            debug("  未指定任何 MenuFeature")
            return
        }
        features.forEach {
            if (!it.isMapped) {
                warning("菜单图标 $char 配置了一项无法识别的 MenuFeature: ${it.reason}")
                return@forEach
            }
            it.handle(event, *args)
            debug("  - ${it.executor!!::class.simpleName}: ${it.data}")
        }
    }

    operator fun component1(): Char = char
    operator fun component2(): ItemStack = icon
    operator fun component3(): List<MappedMenuFeature> = features

    private fun debug(message: String, vararg args: Any) {
        if (!config.isDebug) {
            return
        }
        info(message, *args)
    }

}