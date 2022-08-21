package org.serverct.parrot.parrotx.ui.data

import org.serverct.parrot.parrotx.ui.config.MenuConfiguration
import taboolib.module.ui.ClickEvent

data class ActionContext(
    val config: MenuConfiguration,
    val extra: Map<String, Any?>,
    val event: ClickEvent,
    val args: Collection<Any?>
)
