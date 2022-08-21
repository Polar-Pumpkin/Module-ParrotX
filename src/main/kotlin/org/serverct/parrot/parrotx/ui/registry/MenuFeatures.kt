package org.serverct.parrot.parrotx.ui.registry

import org.serverct.parrot.parrotx.container.SimpleRegistry
import org.serverct.parrot.parrotx.ui.MenuFeature

object MenuFeatures : SimpleRegistry<String, MenuFeature>(HashMap()) {

    override val MenuFeature.key: String
        get() = name.lowercase()

    override fun transformKey(key: String): String = key.lowercase()

}