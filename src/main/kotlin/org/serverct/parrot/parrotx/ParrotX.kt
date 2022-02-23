package org.serverct.parrot.parrotx

import taboolib.common.platform.function.info

object ParrotX {

    var isDebugMode: Boolean = false

    fun debug(message: String, vararg args: Any?) {
        if (isDebugMode) {
            info(message, *args)
        }
    }

}