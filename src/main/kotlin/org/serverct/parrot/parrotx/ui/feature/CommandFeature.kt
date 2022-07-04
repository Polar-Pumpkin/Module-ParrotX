package org.serverct.parrot.parrotx.ui.feature

import org.serverct.parrot.parrotx.function.VariableReaders
import org.serverct.parrot.parrotx.function.adaptList
import org.serverct.parrot.parrotx.ui.MenuFeature
import org.serverct.parrot.parrotx.ui.config.MenuConfiguration
import org.serverct.parrot.parrotx.ui.feature.util.VariableProvider
import taboolib.module.ui.ClickEvent

object CommandFeature : MenuFeature() {

    override val name: String = "Command"

    override fun handle(config: MenuConfiguration, data: Map<*, *>, event: ClickEvent, vararg args: Any?) {
        val commands = data.adaptList<String>("Commands") ?: require("Commands")
        val user = event.clicker
        commands.map { context ->
            VariableReaders.BRACES.replaceNested(context) {
                VariableProvider.Registry[this]?.produce(config, data, event, *args) ?: ""
            }
        }.forEach { user.performCommand(it) }
    }

}