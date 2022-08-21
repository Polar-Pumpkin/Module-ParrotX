package org.serverct.parrot.parrotx.ui.feature

import org.bukkit.inventory.ItemStack
import org.serverct.parrot.parrotx.function.textured
import org.serverct.parrot.parrotx.function.value
import org.serverct.parrot.parrotx.ui.MenuFeature
import org.serverct.parrot.parrotx.ui.data.BuildContext
import taboolib.library.xseries.XMaterial

@Suppress("unused")
object HeadFeature : MenuFeature() {

    override val name: String = "Head"

    override fun build(context: BuildContext): ItemStack {
        val (_, extra, _, _, icon, _) = context
        if (!XMaterial.PLAYER_HEAD.isSimilar(icon)) {
            return icon
        }
        return icon textured extra.value("texture")
    }

}