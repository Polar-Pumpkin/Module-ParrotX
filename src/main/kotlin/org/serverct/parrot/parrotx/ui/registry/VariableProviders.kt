package org.serverct.parrot.parrotx.ui.registry

import org.serverct.parrot.parrotx.container.SimpleRegistry
import org.serverct.parrot.parrotx.ui.feature.util.VariableProvider

object VariableProviders : SimpleRegistry<String, VariableProvider>(HashMap()) {

    override val VariableProvider.key: String
        get() = name.lowercase()

    override fun transformKey(key: String): String = key.lowercase()

}