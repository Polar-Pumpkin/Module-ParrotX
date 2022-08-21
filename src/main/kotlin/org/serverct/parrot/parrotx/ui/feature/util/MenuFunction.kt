package org.serverct.parrot.parrotx.ui.feature.util

import org.bukkit.inventory.ItemStack
import org.serverct.parrot.parrotx.ui.data.ActionContext
import org.serverct.parrot.parrotx.ui.data.BuildContext

interface MenuFunction {

    val name: String

    fun build(context: BuildContext): ItemStack

    fun handle(context: ActionContext)

}
@Suppress("unused")
class MenuFunctionBuilder(name: String? = null, builder: MenuFunctionBuilder.() -> Unit) : MenuFunction {

    override var name: String = name ?: ""
        internal set

    init {
        builder()
    }

    private var builder: (BuildContext) -> ItemStack = { it.icon }
    private var handler: (ActionContext) -> Unit = {}

    fun onBuild(block: (BuildContext) -> ItemStack) {
        builder = block
    }

    fun onHandle(block: (ActionContext) -> Unit) {
        handler = block
    }

    override fun build(context: BuildContext): ItemStack = builder(context)

    override fun handle(context: ActionContext) = handler(context)

}