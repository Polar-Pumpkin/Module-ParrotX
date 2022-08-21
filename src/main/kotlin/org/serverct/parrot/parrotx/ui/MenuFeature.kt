package org.serverct.parrot.parrotx.ui

import org.bukkit.inventory.ItemStack
import org.serverct.parrot.parrotx.ui.data.ActionContext
import org.serverct.parrot.parrotx.ui.data.BuildContext

abstract class MenuFeature {

    abstract val name: String

    open fun build(context: BuildContext): ItemStack = context.icon

    open fun handle(context: ActionContext) {}

}