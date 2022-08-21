package org.serverct.parrot.parrotx.ui.data

import org.bukkit.inventory.ItemStack
import org.serverct.parrot.parrotx.ui.config.MenuConfiguration

data class BuildContext(
    val config: MenuConfiguration,
    val extra: Map<String, Any?>,
    val slot: Int,
    val index: Int,
    val icon: ItemStack,
    val args: List<Any?>
)
