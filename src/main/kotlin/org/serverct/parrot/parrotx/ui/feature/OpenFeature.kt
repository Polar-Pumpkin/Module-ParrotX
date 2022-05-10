package org.serverct.parrot.parrotx.ui.feature

import org.serverct.parrot.parrotx.function.getData
import org.serverct.parrot.parrotx.ui.config.MenuConfiguration
import org.serverct.parrot.parrotx.ui.MenuFeature
import org.serverct.parrot.parrotx.ui.feature.util.MenuOpener
import taboolib.module.ui.ClickEvent

object OpenFeature : MenuFeature() {

    override val name: String = "Open"

    override fun handle(config: MenuConfiguration, data: Map<*, *>, event: ClickEvent, vararg args: Any?) {
        val keyword = data.getData<String>("Keyword")
        val opener = MenuOpener.Registry[keyword] ?: error("未知的 MenuOpener: $keyword")
        opener.open(config, data, event, *args)
    }

}