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

    fun title(variables: Map<String, String> = emptyMap()): String {
        return reader.replaceNested(source.getString(Option.TITLE.path) ?: Option.TITLE.missing()) { variables[this] ?: "" }
    }

    operator fun component1(): ShapeConfiguration = shape
    operator fun component2(): TemplateConfiguration = templates
    operator fun component3(): KeywordConfiguration = keywords

}