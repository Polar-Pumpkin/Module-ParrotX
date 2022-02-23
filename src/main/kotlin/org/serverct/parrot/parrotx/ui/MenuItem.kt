package org.serverct.parrot.parrotx.ui

import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.warning
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.XItemStack
import taboolib.module.ui.ClickEvent

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
        XItemStack.deserialize(section) ?: error("无法获取菜单图标的展示物品"),
        MenuFeature.mapAll(config, section.getMapList("feature"))
    )

    val icon: ItemStack
        get() = internalIcon.clone()

    override fun buildIcon(icon: ItemStack, vararg args: Any?): ItemStack {
        var result = icon.clone()
        features.forEach {
            if (!it.isMapped) {
                warning("菜单图标 $char 配置了一项无法识别的 MenuFeature: ${it.reason}")
                return@forEach
            }
            result = it.buildIcon(icon, *args)
        }
        return result
    }

    override fun handle(event: ClickEvent, vararg args: Any?) {
        if (features.isEmpty()) {
            event.isCancelled = true
            return
        }
        features.forEach {
            if (!it.isMapped) {
                warning("菜单图标 $char 配置了一项无法识别的 MenuFeature: ${it.reason}")
                return@forEach
            }
            it.handle(event, *args)
        }
    }

    operator fun component1(): Char = char

    operator fun component2(): ItemStack = icon

    operator fun component3(): List<MappedMenuFeature> = features

}