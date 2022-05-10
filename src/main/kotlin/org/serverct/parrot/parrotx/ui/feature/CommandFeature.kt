package org.serverct.parrot.parrotx.ui.feature

import org.serverct.parrot.parrotx.function.basicReader
import org.serverct.parrot.parrotx.function.getAdaptedList
import org.serverct.parrot.parrotx.ui.config.MenuConfiguration
import org.serverct.parrot.parrotx.ui.MenuFeature
import org.serverct.parrot.parrotx.ui.feature.util.VariableProvider
import taboolib.module.ui.ClickEvent

object CommandFeature : MenuFeature() {

    override val name: String = "Command"

    override fun handle(config: MenuConfiguration, data: Map<*, *>, event: ClickEvent, vararg args: Any?) {
        val commands = data.getAdaptedList<String>("Commands") ?: require("Commands")
        val user = event.clicker
        commands.map { context ->
            basicReader.replaceNested(context) {
                VariableProvider.Registry[this]?.produce(config, data, event, *args) ?: ""
            }
        }.forEach { user.performCommand(it) }
    }

}