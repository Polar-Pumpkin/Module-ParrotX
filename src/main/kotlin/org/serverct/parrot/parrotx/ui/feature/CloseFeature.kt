package org.serverct.parrot.parrotx.ui.feature

import org.serverct.parrot.parrotx.ui.MenuFeature
import org.serverct.parrot.parrotx.ui.data.ActionContext
import taboolib.common.platform.function.submit

@Suppress("unused")
object CloseFeature : MenuFeature() {

    override val name: String = "Close"

    override fun handle(context: ActionContext) {
        submit {
            context.event.clicker.closeInventory()
        }
    }

}