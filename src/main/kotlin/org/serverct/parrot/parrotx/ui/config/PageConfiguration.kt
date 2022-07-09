package org.serverct.parrot.parrotx.ui.config

import org.serverct.parrot.parrotx.container.SimpleRegistry
import org.serverct.parrot.parrotx.ui.MenuItem
import org.serverct.parrot.parrotx.ui.config.PageConfiguration.Pageable
import java.util.function.Supplier
import java.util.stream.Collectors

@Suppress("MemberVisibilityCanBePrivate")
class PageConfiguration(val holder: MenuConfiguration) : SimpleRegistry<String, Pageable<*>>() {

    override val registered: MutableMap<String, Pageable<*>> = HashMap()
    override val Pageable<*>.key: String
        get() = name

    @Suppress("UNCHECKED_CAST")
    inline operator fun <reified E> invoke(name: String, keyword: String = name, shaper: PageableShaper<E>) {
        if (name !in this) {
            error("未定义可分页内容: $name")
        }
        val pageable = get(name) as? Pageable<E> ?: error("可分页内容 $name 的类型不匹配")
        pageable.generate()

        val iterator = pageable.elements.iterator()
        holder.shape(keyword) { slot, index, item ->
            if (!iterator.hasNext()) {
                return@shape
            }
            val element = iterator.next()
            pageable.mapped[slot] = element
            shaper.execute(slot, index, item, element)
        }
    }

    data class Pageable<E>(val name: String, val patch: Int, val supplier: Supplier<Collection<E>>) {
        internal lateinit var cached: Collection<E>
        val mapped: MutableMap<Int, E> = HashMap()

        var page: Int = 0
            private set
        val maximum: Int
            get() = cached.size / patch

        val elements: List<E>
            get() = cached.stream()
                .skip((page * patch).toLong())
                .limit(patch.toLong())
                .collect(Collectors.toList())

        fun generate() {
            cached = supplier.get()
        }

        operator fun get(slot: Int): E? = mapped[slot]

        fun hasNext(): Boolean = page < maximum

        fun hasPrevious(): Boolean = page > 0

        fun next(cycle: Boolean = false) {
            if (hasNext()) {
                page++
                return
            }
            if (cycle) {
                page = 0
            }
        }

        fun previous(cycle: Boolean = false) {
            if (hasPrevious()) {
                page--
                return
            }
            if (cycle) {
                page = maximum
            }
        }
    }

    fun interface PageableShaper<E> {
        fun execute(slot: Int, index: Int, item: MenuItem, element: E)
    }

}