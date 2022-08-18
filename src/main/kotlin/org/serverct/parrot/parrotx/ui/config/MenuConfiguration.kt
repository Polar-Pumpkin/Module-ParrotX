package org.serverct.parrot.parrotx.ui.config

import org.serverct.parrot.parrotx.ui.MenuFeatureBase
import taboolib.common.util.VariableReader
import taboolib.module.configuration.Configuration

@Suppress("MemberVisibilityCanBePrivate")
class MenuConfiguration(internal val source: Configuration) : MenuFeatureBase() {

    val reader by lazy { VariableReader("{", "}") }
    val isDebug by lazy { source.getBoolean("Debug", false) }

    val shape: ShapeConfiguration by lazy { ShapeConfiguration(this) }
    val templates: TemplateConfiguration by lazy { TemplateConfiguration(this) }
    val keywords: KeywordConfiguration by lazy { KeywordConfiguration(this) }
    val cached: MutableMap<String, Any?> by lazy { HashMap() }
    val mapped: MutableMap<Int, Any?> by lazy { HashMap() }

    fun title(vararg variables: Pair<String, () -> String>): String {
        val map = variables.toMap()
        return reader.replaceNested(source.getString(Option.TITLE.path) ?: Option.TITLE.missing()) { map[this]?.invoke() ?: "" }
    }

    operator fun component1(): ShapeConfiguration = shape
    operator fun component2(): TemplateConfiguration = templates
    operator fun component3(): KeywordConfiguration = keywords
    operator fun component4(): MenuConfiguration = this
    operator fun component5(): MutableMap<String, Any?> = cached
    operator fun component6(): MutableMap<Int, Any?> = mapped

}