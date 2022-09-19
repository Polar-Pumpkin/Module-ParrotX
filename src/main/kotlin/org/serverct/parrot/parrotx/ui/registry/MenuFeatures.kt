package org.serverct.parrot.parrotx.ui.registry

import org.serverct.parrot.parrotx.container.SimpleRegistry
import org.serverct.parrot.parrotx.ui.MenuFeature
import java.util.*

object MenuFeatures : SimpleRegistry<String, MenuFeature>(TreeMap(String.CASE_INSENSITIVE_ORDER)) {
    override fun getKey(value: MenuFeature): String = value.name
}