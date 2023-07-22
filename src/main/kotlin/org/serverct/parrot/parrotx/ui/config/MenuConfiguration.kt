package org.serverct.parrot.parrotx.ui.config

import org.serverct.parrot.parrotx.function.VariableReaders
import org.serverct.parrot.parrotx.function.oneOf
import org.serverct.parrot.parrotx.ui.config.advance.KeywordConfiguration
import org.serverct.parrot.parrotx.ui.config.advance.ShapeConfiguration
import org.serverct.parrot.parrotx.ui.config.advance.TemplateConfiguration
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration
import taboolib.module.ui.type.Linked

@Suppress("MemberVisibilityCanBePrivate", "unused")
class MenuConfiguration(internal val source: Configuration) {

    val isDebug: Boolean by lazy { source.oneOf(*MenuPart.DEBUG.paths, getter = ConfigurationSection::getBoolean) ?: false }

    val title: String? by lazy { source.oneOf(*MenuPart.TITLE.paths, getter = ConfigurationSection::getString) }
    val shape: ShapeConfiguration by lazy { ShapeConfiguration(this) }
    val templates: TemplateConfiguration by lazy { TemplateConfiguration(this) }
    val keywords: KeywordConfiguration by lazy { KeywordConfiguration(this) }
    val cached: MutableMap<String, Any?> by lazy { HashMap() }
    val mapped: MutableMap<Int, Any?> by lazy { HashMap() }

    fun title(vararg variables: Pair<String, () -> String>): String {
        return with(variables.toMap()) {
            VariableReaders.BRACES.replaceNested(title ?: MenuPart.TITLE.missing()) {
                get(this)?.invoke() ?: ""
            }
        }
    }

    fun setPreviousPage(menu: Linked<*>, keyword: String = "Previous") {
        shape[keyword].first().let { slot ->
            menu.setPreviousPage(slot) { _, it ->
                templates(keyword, slot, 0, !it)
            }
        }
    }

    fun setNextPage(menu: Linked<*>, keyword: String = "Next") {
        shape[keyword].first().let { slot ->
            menu.setNextPage(slot) { _, it ->
                templates(keyword, slot, 0, !it)
            }
        }
    }

    operator fun component1(): ShapeConfiguration = shape
    operator fun component2(): TemplateConfiguration = templates
    operator fun component3(): KeywordConfiguration = keywords
    operator fun component4(): MenuConfiguration = this
    operator fun component5(): MutableMap<String, Any?> = cached
    operator fun component6(): MutableMap<Int, Any?> = mapped

}