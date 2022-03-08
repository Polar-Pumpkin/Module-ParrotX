package org.serverct.parrot.parrotx.ui

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.serverct.parrot.parrotx.function.mapAs
import org.serverct.parrot.parrotx.ui.feature.FunctionalFeature
import taboolib.common.util.VariableReader
import taboolib.module.configuration.Configuration

@Suppress("MemberVisibilityCanBePrivate")
class MenuConfiguration(private val identity: String, private val source: Configuration) : MenuFeatureBase() {

    val reader by lazy { VariableReader("{", "}") }
    val isDebug by lazy { source.getBoolean("Debug", false) }

    val rows: Int by lazy {
        source.getInt(Option.ROWS.path, -1).also {
            if (it <= 0) {
                Option.ROWS.missing()
            }
        }
    }
    val range: IntRange by lazy { 0 until (rows * 9) }

    val shape: Collection<String> by lazy {
        source.getStringList(Option.SHAPE.path).also { shape ->
            if (shape.isEmpty()) {
                Option.SHAPE.missing()
            }
            if (!shape.all { it.length == 9 }) {
                Option.SHAPE.incorrect("每行需定义 9 个字符")
            }
            if (shape.size != rows) {
                Option.SHAPE.incorrect("行数需与${Option.ROWS.formatted}相等")
            }
        }
    }
    val shapeArray: Array<String> by lazy { shape.toTypedArray() }
    val shapeFlatten: String by lazy { shape.joinToString("") }

    val templates: Map<Char, MenuItem> by lazy {
        source.mapAs(Option.TEMPLATE.path) { getConfigurationSection(it) }
            .mapNotNull { (key, value) ->
                runCatching {
                    MenuItem(this, value)
                }.onFailure {
                    Option.TEMPLATE.exception("加载 $key 时遇到错误", it).printStackTrace()
                }.getOrNull()
            }
            .associateBy { it.char }
            .toMutableMap()
            .apply { putIfAbsent(' ', MenuItem(this@MenuConfiguration, ' ', ItemStack(Material.AIR), ArrayList())) }
    }
    val keywords: BiMap<String, Char> by lazy {
        // HashBiMap.create(source.mapAs("Keyword") { getString(it)?.first() })
        val map: BiMap<String, Char> = HashBiMap.create()
        templates.forEach { char, (_, _, features) ->
            val functionals = features.filter { it.executor is FunctionalFeature }
            if (functionals.isEmpty()) {
                return@forEach
            }

            val keyword = runCatching {
                functionals.let {
                    if (it.size > 1) {
                        Option.TEMPLATE.exception("项目 $char 存在多个 FunctionalFeature").printStackTrace()
                    }
                    val feature = it.first()
                    (feature.executor!! as FunctionalFeature).keyword(feature.data)
                }
            }.onFailure {
                Option.TEMPLATE.exception("获取项目 $char 的 FunctionalFeature 关键词时遇到错误", it).printStackTrace()
            }.getOrNull() ?: return@forEach
            map[keyword] = char
        }
        map
    }

    fun title(vararg variables: Pair<String, String>): String {
        val title = source.getString(Option.TITLE.path) ?: Option.TITLE.missing()
        val variableMap = variables.toMap()
        return reader.replaceNested(title) { variableMap[this] ?: "" }
    }

    fun shape(vararg ignores: String, action: (Int, Triple<String, String, String>, MenuItem) -> Unit) {
        shapeFlatten.forEachIndexed { index, char ->
            val template = templates[char] ?: Option.SHAPE.incorrect("存在${Option.TEMPLATE.formatted}中未定义的字符: $char")

            val context = keywords.inverse()[char]
            if (context in ignores) {
                return@forEachIndexed
            }
            action(index, context.menuKeyword, template)
        }
    }

    fun shapeOf(keyword: String, action: (Int, Int, MenuItem) -> Unit) {
        val template = templateOf(keyword)
        indexOf(keyword, allowMulti = true).forEachIndexed { index, slot ->
            action(index, slot, template)
        }
    }

    fun charAt(index: Int): Char {
        if (!indexIn(index)) {
            error("获取指定位置的字符时传入索引值错误: $index.")
        }
        return shapeFlatten.elementAt(index)
    }

    fun keywordAt(index: Int): String? = keywords.inverse()[charAt(index)]

    fun templateOf(keyword: String): MenuItem {
        val char = keywords[keyword] ?: Option.TEMPLATE.incorrect("缺少与 Functional 关键词 $keyword 相关联的项目")
        return templates[char]!!
    }

    fun templateOfOrNull(keyword: String): MenuItem? {
        return templates[keywords[keyword] ?: return null]
    }

    fun templateAt(index: Int): MenuItem {
        val char = charAt(index)
        return templates[char] ?: Option.TEMPLATE.incorrect("未配置字符 $char 对应项目")
    }

    fun templateAtOrNull(index: Int): MenuItem? {
        return templates[shapeFlatten.elementAtOrNull(index) ?: return null]
    }

    fun indexIn(index: Int): Boolean = index in range

    fun indexOf(keyword: String, allowEmpty: Boolean = false, allowMulti: Boolean = false): Set<Int> {
        val indexes = LinkedHashSet<Int>()

        val ref = keywords[keyword]
        if (ref != null) {
            shapeFlatten.forEachIndexed { index, char ->
                if (char == ref) {
                    indexes += index
                }
            }
        }

        if (!allowEmpty && indexes.isEmpty()) {
            Option.SHAPE.incorrect("未映射关键词 $keyword($ref)")
        }
        if (!allowMulti && indexes.size > 1) {
            Option.SHAPE.incorrect("关键词 $keyword($ref) 映射了多个位置")
        }
        return indexes
    }

    fun iconOf(keyword: String, fallback: Boolean, vararg args: Any?): ItemStack {
        if (fallback) {
            return templateOfOrNull("Fallback")?.iconWith(*args) ?: ItemStack(Material.AIR)
        }
        return templateOf(keyword).iconWith(*args)
    }

    private enum class Option(val path: String, val display: String) {
        TITLE("Title", "标题"),
        ROWS("Rows", "行数"),
        SHAPE("Shape", "样式映射"),
        TEMPLATE("Template", "物品模板");

        val formatted: String
            get() = "$display($path)"

        fun missing(): Nothing = error("缺失 Gui 配置: $formatted")

        fun incorrect(reason: String): Nothing = error("Gui 配置不正确@$formatted: $reason")

        fun exception(message: String, cause: Throwable? = null): IllegalStateException {
            val context = "Gui 配置不正确@$formatted: $message"
            return if (cause == null) IllegalStateException(context) else IllegalStateException(context, cause)
        }
    }

}