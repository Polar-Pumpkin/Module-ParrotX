package org.serverct.parrot.parrotx.ui.registry

import org.serverct.parrot.parrotx.container.SimpleRegistry
import org.serverct.parrot.parrotx.ui.feature.util.MenuFunction
import java.util.*

object MenuFunctions : SimpleRegistry<String, MenuFunction>(TreeMap(String.CASE_INSENSITIVE_ORDER)) {
    override fun getKey(value: MenuFunction): String = value.name
}