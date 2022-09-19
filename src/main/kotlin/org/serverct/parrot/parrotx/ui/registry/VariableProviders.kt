package org.serverct.parrot.parrotx.ui.registry

import org.serverct.parrot.parrotx.container.SimpleRegistry
import org.serverct.parrot.parrotx.ui.feature.util.VariableProvider
import java.util.*

object VariableProviders : SimpleRegistry<String, VariableProvider>(TreeMap(String.CASE_INSENSITIVE_ORDER)) {
    override fun getKey(value: VariableProvider): String = value.name
}