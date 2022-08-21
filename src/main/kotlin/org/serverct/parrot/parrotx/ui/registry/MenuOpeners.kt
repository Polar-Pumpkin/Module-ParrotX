package org.serverct.parrot.parrotx.ui.registry

import org.serverct.parrot.parrotx.container.SimpleRegistry
import org.serverct.parrot.parrotx.ui.feature.util.MenuOpener

object MenuOpeners : SimpleRegistry<String, MenuOpener>(HashMap()) {

    override val MenuOpener.key: String
        get() = name.lowercase()

    override fun transformKey(key: String): String = key.lowercase()

}