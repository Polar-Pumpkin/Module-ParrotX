package org.serverct.parrot.parrotx.ui.feature

import org.serverct.parrot.parrotx.function.VariableReaders
import org.serverct.parrot.parrotx.function.asList
import org.serverct.parrot.parrotx.ui.MenuFeature
import org.serverct.parrot.parrotx.ui.data.ActionContext
import org.serverct.parrot.parrotx.ui.registry.VariableProviders

@Suppress("unused")
object CommandFeature : MenuFeature() {

    override val name: String = "Command"

    override fun handle(context: ActionContext) {
        val (_, extra, _, event, _) = context
        val commands = extra.asList<String>("commands") ?: return
        val user = event.clicker
        commands.map {
            VariableReaders.BRACES.replaceNested(it) {
                VariableProviders[this]?.produce(context) ?: ""
            }
        }.forEach(user::performCommand)
    }

}