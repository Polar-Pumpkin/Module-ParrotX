package org.serverct.parrot.parrotx.ui

import org.bukkit.inventory.ItemStack
import taboolib.module.ui.ClickEvent

@Suppress("MemberVisibilityCanBePrivate")
data class MappedMenuFeature(
    val config: MenuConfiguration,
    val data: Map<*, *> = emptyMap<Any, Any>(),
    val executor: MenuFeature? = null,
    val reason: String = ""
) : ParsedMenuFeature() {

    val isMapped: Boolean
        get() = executor != null

    override fun buildIcon(icon: ItemStack, vararg args: Any?): ItemStack {
        return executor?.buildIcon(config, data, icon, *args) ?: icon
    }

    override fun handle(event: ClickEvent, vararg args: Any?) {
        executor?.handle(config, data, event, *args)
    }

    companion object {
        fun accept(config: MenuConfiguration, data: Map<*, *>, feature: MenuFeature): MappedMenuFeature =
            MappedMenuFeature(config, data, feature)

        fun deny(config: MenuConfiguration, reason: String): MappedMenuFeature = MappedMenuFeature(config, reason = reason)

        fun map(config: MenuConfiguration, data: Map<*, *>): MappedMenuFeature {
            val type = (data["=="] ?: data["Type"] ?: return deny(config, "未指定 Feature 类型")).toString()
            val feature = MenuFeature.Registry[type] ?: return deny(config, "未知的 Feature 类型: $type")
            return accept(config, data, feature)
        }

        fun mapAll(config: MenuConfiguration, datas: List<Map<*, *>>): List<MappedMenuFeature> = datas.map { map(config, it) }
    }

}