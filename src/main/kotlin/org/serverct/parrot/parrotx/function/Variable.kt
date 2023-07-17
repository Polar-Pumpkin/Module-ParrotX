@file:Suppress("unused")

package org.serverct.parrot.parrotx.function

import taboolib.common.util.VariableReader
import java.util.*

object VariableReaders {
    val BRACES by lazy { VariableReader("{", "}") }
    val DOUBLE_BRACES by lazy { VariableReader("{{", "}}") }
    val PERCENT by lazy { VariableReader("%", "%") }

    internal val AREA_START by lazy { "^#area (?<area>.+)$".toRegex() }
    internal val AREA_END by lazy { "^#end(?: (?<area>.+))?$".toRegex() }
}

fun interface VariableTransformer {
    fun transfer(name: String): Collection<String>?
}

class VariableTransformerBuilder(builder: VariableTransformerBuilder.() -> Unit) : VariableTransformer {

    private val registered: MutableMap<String, (String) -> Collection<String>?> = mutableMapOf()
    private var def: (String) -> Collection<String>? = { null }

    init {
        builder()
    }

    @JvmName("defaultLines")
    fun default(func: (String) -> Collection<String>?) {
        this.def = func
    }

    @JvmName("defaultLine")
    fun default(func: (String) -> String?) {
        this.def = { func(it)?.let(::listOf) }
    }

    @JvmName("nameLines")
    fun name(name: String, func: (String) -> Collection<String>?) {
        registered[name] = func
    }

    @JvmName("nameLine")
    fun name(name: String, func: (String) -> String?) {
        registered[name] = { func(it)?.let(::listOf) }
    }

    override fun transfer(name: String): Collection<String>? = registered[name]?.invoke(name) ?: def.invoke(name)

}

fun Collection<String>.variables(reader: VariableReader = VariableReaders.BRACES, transformer: VariableTransformer): List<String> {
    return flatMap { context ->
        val result = ArrayList<String>()
        val queued = HashMap<String, Queue<String>>()
        reader.replaceNested(context) scan@{
            queued[this] = LinkedList(transformer.transfer(this) ?: return@scan this)
            this
        }
        if (queued.isEmpty()) {
            return@flatMap listOf(context)
        }

        while (queued.any { (_, queue) -> queue.isNotEmpty() }) {
            result += reader.replaceNested(context) {
                if (this in queued) {
                    queued[this]!!.poll() ?: ""
                } else {
                    this
                }
            }
        }
        result
    }
}

fun Collection<String>.transform(reader: VariableReader = VariableReaders.BRACES, builder: VariableTransformerBuilder.() -> Unit): List<String> {
    return variables(reader, VariableTransformerBuilder(builder))
}

fun Collection<String>.variable(key: String, value: Collection<String>, reader: VariableReader = VariableReaders.BRACES): List<String> {
    return variables(reader) { if (it == key) value else null }
}

fun Collection<String>.singletons(reader: VariableReader = VariableReaders.BRACES, transfer: (String) -> String?): List<String> {
    return variables(reader) { transfer(it)?.let(::listOf) }
}

fun Collection<String>.singleton(key: String, value: String, reader: VariableReader = VariableReaders.BRACES): List<String> {
    return singletons(reader) { if (it == key) value else null }
}

fun interface AreaFilter {
    fun filter(name: String): Boolean
}

class AreaFilterBuilder(builder: AreaFilterBuilder.() -> Unit) : AreaFilter {

    private val registered: MutableMap<String, (String) -> Boolean> = mutableMapOf()
    private var def = false

    init {
        builder()
    }

    fun default(value: Boolean) {
        def = value
    }

    fun name(name: String, func: (String) -> Boolean) {
        registered[name] = func
    }

    override fun filter(name: String): Boolean = registered[name]?.invoke(name) ?: def

}

fun Iterable<String>.areas(filter: AreaFilter): List<String> {
    val selected: MutableList<String> = ArrayList()
    val areas: Deque<String> = LinkedList()

    val iterator = iterator()
    while (iterator.hasNext()) {
        val line = iterator.next()

        val ender = VariableReaders.AREA_END.find(line)
        if (ender != null) {
            if (areas.isNotEmpty()) {
                val leaved = (ender.groups as MatchNamedGroupCollection)["area"]?.value
                if (leaved == null) {
                    areas.pop()
                } else {
                    if (leaved in areas) {
                        while (areas.isNotEmpty() && areas.pop() != leaved) {
                            // DO NOTHING
                        }
                    }
                }
            }
            continue
        }

        val area = areas.peek()
        if (area != null && !filter.filter(area)) {
            continue
        }

        val starter = VariableReaders.AREA_START.find(line)
        if (starter != null) {
            val entered = (starter.groups as MatchNamedGroupCollection)["area"]!!.value
            if (entered !in areas) {
                areas.push(entered)
            }
            continue
        }
        selected.add(line)
    }
    return selected
}

fun Iterable<String>.areas(builder: AreaFilterBuilder.() -> Unit): List<String> {
    return areas(AreaFilterBuilder(builder))
}