package org.serverct.parrot.parrotx.ui.config

import org.serverct.parrot.parrotx.ui.MenuItem
import org.serverct.parrot.parrotx.ui.MenuKeyword

@Suppress("MemberVisibilityCanBePrivate")
class ShapeConfiguration(val holder: MenuConfiguration) {

    val raw: List<String> = holder.source.getStringList(Option.SHAPE.path)

    val row: Int = raw.size

    init {
        if (row == 0) {
            Option.SHAPE.missing()
        }
    }

    val range: IntRange by lazy { 0 until (row * 9) }

    val lines: List<String> by lazy {
        raw.map {
            val length = it.length
            if (length == 9) {
                it
            } else if (length < 9) {
                it + " ".repeat(9 - length)
            } else {
                it.substring(0, 9)
            }
        }
    }

    val array: Array<String> by lazy { lines.toTypedArray() }

    val flatten: String by lazy { lines.joinToString("") }

    operator fun get(slot: Int): Char = flatten.elementAtOrNull(slot) ?: error("尝试获取越界槽位的字符: $slot")

    operator fun contains(slot: Int): Boolean = slot in range

    operator fun get(keyword: String, empty: Boolean = false, multi: Boolean = true): Set<Int> {
        val indexes = HashSet<Int>()
        val ref = holder.keywords[keyword]
        if (ref != null) {
            flatten.forEachIndexed { index, char ->
                if (char == ref) {
                    indexes += index
                }
            }
        }

        if (!empty && indexes.isEmpty()) {
            Option.SHAPE.incorrect("未映射 Functional 关键词 $keyword($ref)")
        }
        if (!multi && indexes.size > 1) {
            Option.SHAPE.incorrect("Functional 关键词 $keyword($ref) 映射了多个位置")
        }
        return indexes
    }

    operator fun invoke(keyword: String, shaper: SimpleShaper) {
        val template = holder.templates.require(keyword)
        this[keyword].forEachIndexed { index, slot ->
            shaper.execute(slot, index, template)
        }
    }

    operator fun invoke(vararg ignores: String, shaper: MultiShaper) {
        val repeats: MutableMap<Char, Int> = HashMap()
        flatten.forEachIndexed { slot, char ->
            val template = holder.templates.require(char)
            val keyword = holder.keywords[char]
            if (keyword != null && keyword in ignores) {
                return@forEachIndexed
            }
            shaper.execute(slot, repeats.compute(char) { _, current -> (current ?: -1) + 1 }!!, template, MenuKeyword.of(keyword))
        }
    }

    fun interface SimpleShaper {
        fun execute(slot: Int, index: Int, item: MenuItem)
    }

    fun interface MultiShaper {
        fun execute(slot: Int, index: Int, item: MenuItem, keyword: MenuKeyword)
    }

}