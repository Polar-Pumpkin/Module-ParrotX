package org.serverct.parrot.parrotx.ui.feature

import org.serverct.parrot.parrotx.function.value
import org.serverct.parrot.parrotx.ui.MenuFeature
import org.serverct.parrot.parrotx.ui.data.ActionContext
import org.serverct.parrot.parrotx.ui.registry.MenuOpeners

@Suppress("unused")
object OpenFeature : MenuFeature() {

    override val name: String = "Open"

    override fun handle(context: ActionContext) {
        val keyword = context.extra.value<String>("keyword")
        requireNotNull(MenuOpeners[keyword]) { "未知的 MenuOpener: $keyword" }(context)
    }

}