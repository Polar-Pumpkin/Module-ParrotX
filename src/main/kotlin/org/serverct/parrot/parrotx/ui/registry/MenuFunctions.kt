package org.serverct.parrot.parrotx.ui.registry

import org.serverct.parrot.parrotx.container.SimpleRegistry
import org.serverct.parrot.parrotx.ui.feature.util.MenuFunction

object MenuFunctions : SimpleRegistry<String, MenuFunction>(HashMap()) {

    override val MenuFunction.key: String
        get() = name.lowercase()

    override fun transformKey(key: String): String = key.lowercase()

}