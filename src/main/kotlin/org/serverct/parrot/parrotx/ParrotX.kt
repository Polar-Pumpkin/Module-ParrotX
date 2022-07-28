package org.serverct.parrot.parrotx

import taboolib.common.platform.function.info
import taboolib.common.util.replaceWithOrder

@Suppress("MemberVisibilityCanBePrivate", "unused")
object ParrotX {

    val isDebugMode: Boolean by lazy { System.getProperty("parrotx.debug", "false").toBooleanStrict() }

    fun debug(message: String, vararg args: Any?) {
        if (isDebugMode) {
            info(message.replaceWithOrder(*args.map { it ?: "null" }.toTypedArray()))
        }
    }

}