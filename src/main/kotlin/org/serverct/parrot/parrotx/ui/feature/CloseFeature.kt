package org.serverct.parrot.parrotx.ui.feature

import org.serverct.parrot.parrotx.ui.MenuConfiguration
import org.serverct.parrot.parrotx.ui.MenuFeature
import taboolib.common.platform.function.submit
import taboolib.module.ui.ClickEvent

object CloseFeature : MenuFeature() {

    override val name: String = "Close"

    override fun handle(config: MenuConfiguration, data: Map<*, *>, event: ClickEvent, vararg args: Any?) {
        submit { event.clicker.closeInventory() }
    }

}