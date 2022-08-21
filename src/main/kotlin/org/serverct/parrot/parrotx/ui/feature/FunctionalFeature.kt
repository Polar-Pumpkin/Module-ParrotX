package org.serverct.parrot.parrotx.ui.feature

import org.bukkit.inventory.ItemStack
import org.serverct.parrot.parrotx.function.value
import org.serverct.parrot.parrotx.ui.MenuFeature
import org.serverct.parrot.parrotx.ui.data.ActionContext
import org.serverct.parrot.parrotx.ui.data.BuildContext
import org.serverct.parrot.parrotx.ui.registry.MenuFunctions

object FunctionalFeature : MenuFeature() {

    override val name: String = "Functional"

    override fun build(context: BuildContext): ItemStack = MenuFunctions[keyword(context.extra)]?.build(context) ?: context.icon

    override fun handle(context: ActionContext) {
        MenuFunctions[keyword(context.extra)]?.handle(context)
    }

    fun keyword(extra: Map<*, *>): String = extra.value("keyword")

}