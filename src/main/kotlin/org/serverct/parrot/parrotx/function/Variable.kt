package org.serverct.parrot.parrotx.function

import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import taboolib.common.util.VariableReader
import taboolib.module.chat.colored
import taboolib.platform.util.modifyMeta
import java.util.*

object VariableReaders {
    val BRACES by lazy { VariableReader("{", "}") }
    val DOUBLE_BRACES by lazy { VariableReader("{{", "}}") }
    val PERCENT by lazy { VariableReader("%", "%") }
}

fun ItemStack.variables(reader: VariableReader = VariableReaders.BRACES, transfer: (String) -> Collection<String>): ItemStack {
    return modifyMeta<ItemMeta> {
        displayName = displayName?.let {
            reader.replaceNested(it) { transfer(this).firstOrNull() ?: "-" }.colored()
        }
        lore = lore?.variables(reader, transfer)?.colored()
    }
}

fun ItemStack.singletons(reader: VariableReader = VariableReaders.BRACES, transfer: (String) -> String): ItemStack {
    return variables(reader) { listOf(transfer(it)) }
}

fun Collection<String>.variables(reader: VariableReader = VariableReaders.BRACES, transfer: (String) -> Collection<String>): List<String> {
    return flatMap { context ->
        val result = ArrayList<String>()
        val queued = HashMap<String, Queue<String>>()
        reader.replaceNested(context) {
            queued[this] = LinkedList(transfer(this))
            this
        }
        if (queued.isEmpty()) {
            return@flatMap listOf(context)
        }

        while (queued.any { (_, queue) -> queue.isNotEmpty() }) {
            result += reader.replaceNested(context) {
                (queued[this]?.poll() ?: "")
            }
        }
        result
    }
}

fun Collection<String>.singletons(reader: VariableReader = VariableReaders.BRACES, transfer: (String) -> String): List<String> {
    return variables(reader) { listOf(transfer(it)) }
}