package org.serverct.parrot.parrotx

import taboolib.common.platform.function.info

@Suppress("MemberVisibilityCanBePrivate")
object ParrotX {

    internal val isDebugMode: Boolean by lazy { System.getProperty("parrotxDebug", "false").toBooleanStrict() }

    fun debug(message: String, vararg args: Any?) {
        if (isDebugMode) {
            info(message, *args)
        }
    }

}