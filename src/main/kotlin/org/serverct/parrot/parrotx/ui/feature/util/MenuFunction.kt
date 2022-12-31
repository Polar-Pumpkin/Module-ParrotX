package org.serverct.parrot.parrotx.ui.feature.util

import org.bukkit.inventory.ItemStack
import org.serverct.parrot.parrotx.ui.data.ActionContext
import org.serverct.parrot.parrotx.ui.data.BuildContext

interface MenuFunction {

    val name: String

    fun build(context: BuildContext): ItemStack

    fun handle(context: ActionContext)

}
@Suppress("MemberVisibilityCanBePrivate", "unused")
class MenuFunctionBuilder(name: String? = null, builder: MenuFunctionBuilder.() -> Unit) : MenuFunction {

    override var name: String = name ?: ""
        internal set

    private var builder: (BuildContext) -> ItemStack = { it.icon }
    private var handler: (ActionContext) -> Unit = {}

    init {
        builder()
    }

    fun onBuild(block: (BuildContext) -> ItemStack) {
        builder = block
    }

    fun onClick(block: (ActionContext) -> Unit) {
        handler = block
    }

    @Deprecated("Counterintuitive naming", ReplaceWith("onClick(block)"))
    fun onHandle(block: (ActionContext) -> Unit) = onClick(block)

    override fun build(context: BuildContext): ItemStack = builder(context)

    override fun handle(context: ActionContext) = handler(context)

}