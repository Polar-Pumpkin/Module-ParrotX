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

    inline operator fun <reified E> invoke(name: String, keyword: String = name, shaper: PageableShaper<E>) {
        val elements = (get(name) ?: error("未定义可分页内容: $name")).elements
        elements.firstOrNull { it !is E }?.let { mismatch ->
            error("可分页内容 $name 内存在类型不匹配的对象: ${mismatch::class.java.name} -> ${E::class.java.name}")
        }

        val iterator = elements.iterator()
        holder.shape(keyword) { slot, index, item ->
            if (!iterator.hasNext()) {
                return@shape
            }
            shaper.execute(slot, index, item, iterator.next() as E)
        }
    }

    data class Pageable<E>(val name: String, val patch: Int, val supplier: Supplier<Collection<E>>) {
        var page: Int = 0
            private set
        val maximum: Int
            get() = supplier.get().size / page

        val elements: List<E>
            get() = supplier.get().stream()
                .skip((page * patch).toLong())
                .limit(patch.toLong())
                .collect(Collectors.toList())

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