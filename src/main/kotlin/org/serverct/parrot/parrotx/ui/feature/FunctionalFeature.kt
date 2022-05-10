package org.serverct.parrot.parrotx.ui.feature

import org.bukkit.inventory.ItemStack
import org.serverct.parrot.parrotx.function.getData
import org.serverct.parrot.parrotx.ui.MenuFeature
import org.serverct.parrot.parrotx.ui.config.MenuConfiguration
import org.serverct.parrot.parrotx.ui.feature.util.MenuFunction
import taboolib.common.platform.function.info
import taboolib.module.ui.ClickEvent

object FunctionalFeature : MenuFeature() {

    override val name: String = "Functional"

    override fun buildIcon(config: MenuConfiguration, data: Map<*, *>, icon: ItemStack, vararg args: Any?): ItemStack {
        val keyword = keyword(data)
        if (config.isDebug) {
            info("[Functional@$keyword] 构建图标")
        }
        return MenuFunction.Registry[keyword]?.buildIcon(config, data, icon, *args) ?: icon
    }

    override fun handle(config: MenuConfiguration, data: Map<*, *>, event: ClickEvent, vararg args: Any?) {
        val keyword = keyword(data)
        if (config.isDebug) {
            info("[Functional@$keyword] 处理点击")
        }
        MenuFunction.Registry[keyword]?.handle(config, data, event, *args)
    }

    fun keyword(data: Map<*, *>): String = data.getData("Keyword")

}