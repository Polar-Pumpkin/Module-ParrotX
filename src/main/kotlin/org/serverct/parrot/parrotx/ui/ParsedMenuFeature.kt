package org.serverct.parrot.parrotx.ui

import org.bukkit.inventory.ItemStack
import taboolib.module.ui.ClickEvent

abstract class ParsedMenuFeature : MenuFeatureBase() {

    abstract fun buildIcon(icon: ItemStack, vararg args: Any?): ItemStack

    abstract fun handle(event: ClickEvent, vararg args: Any?)

}