package org.serverct.parrot.parrotx.function

import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import taboolib.common.util.VariableReader
import taboolib.module.chat.colored
import taboolib.platform.util.modifyMeta
import java.util.*

val basicReader: VariableReader = VariableReader("{", "}")

fun ItemStack.replaceVariables(
    reader: VariableReader = basicReader,
    transfer: (String) -> Collection<String>
): ItemStack {
    return modifyMeta<ItemMeta> {
        displayName = displayName?.let {
            reader.replaceNested(it) { transfer(this).firstOrNull() ?: "-" }.colored()
        }
        lore = lore?.replaceVariables(reader, transfer)?.colored()
    }
}

fun Collection<String>.replaceVariables(
    reader: VariableReader = basicReader,
    transfer: (String) -> Collection<String>
): List<String> {
    return flatMap { context ->
        val result = ArrayList<String>()
        val queued = HashMap<String, Queue<String>>()
        reader.replaceNested(context) {
            transfer(this).let {
                if (it.isEmpty()) {
                    return@let
                }
                queued[this] = LinkedList(it)
            }
            this
        }

        if (queued.isEmpty()) {
            return@flatMap listOf(context)
        }

        while (queued.any { (_, queue) -> queue.isNotEmpty() }) {
            result += reader.replaceNested(context) {
                queued[this]?.poll() ?: "-"
            }
        }
        result
    }
}