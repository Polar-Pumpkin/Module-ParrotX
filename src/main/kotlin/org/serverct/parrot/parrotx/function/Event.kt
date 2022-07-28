@file:Isolated
@file:Suppress("unused")

package org.serverct.parrot.parrotx.function

import org.bukkit.event.Cancellable
import taboolib.common.Isolated
import taboolib.common.platform.function.submit

inline fun <reified T : Cancellable> T.ifNotCancelled(crossinline action: T.() -> Unit): T {
    submit {
        if (!isCancelled) {
            action()
        }
    }
    return this
}

inline fun <reified T : Cancellable> T.ifCancelled(crossinline action: T.() -> Unit): T {
    submit {
        if (isCancelled) {
            action()
        }
    }
    return this
}