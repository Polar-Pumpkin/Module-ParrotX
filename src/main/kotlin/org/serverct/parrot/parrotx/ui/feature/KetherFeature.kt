package org.serverct.parrot.parrotx.ui.feature

import org.serverct.parrot.parrotx.function.adaptList
import org.serverct.parrot.parrotx.ui.config.MenuConfiguration
import org.serverct.parrot.parrotx.ui.MenuFeature
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.kether.KetherShell
import taboolib.module.kether.runKether
import taboolib.module.ui.ClickEvent

object KetherFeature : MenuFeature() {

    override val name: String = "Kether"

    override fun handle(config: MenuConfiguration, data: Map<*, *>, event: ClickEvent, vararg args: Any?) {
        val scripts = data.adaptList<String>("Scripts") ?: require("Scripts")
        runKether {
            KetherShell.eval(scripts, sender = adaptPlayer(event.clicker))
        }
    }

}