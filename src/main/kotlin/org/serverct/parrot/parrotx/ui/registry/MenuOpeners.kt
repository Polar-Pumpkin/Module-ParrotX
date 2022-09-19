package org.serverct.parrot.parrotx.ui.registry

import org.serverct.parrot.parrotx.container.SimpleRegistry
import org.serverct.parrot.parrotx.ui.feature.util.MenuOpener
import java.util.*

object MenuOpeners : SimpleRegistry<String, MenuOpener>(TreeMap(String.CASE_INSENSITIVE_ORDER)) {
    override fun getKey(value: MenuOpener): String = value.name
}