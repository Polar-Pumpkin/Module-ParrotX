package org.serverct.parrot.parrotx.ui.config

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import org.serverct.parrot.parrotx.ui.feature.FunctionalFeature

@Suppress("MemberVisibilityCanBePrivate")
class KeywordConfiguration(val holder: MenuConfiguration) {

    private val keywords: BiMap<String, Char> by lazy {
        HashBiMap.create<String, Char>().apply {
            holder.templates.forEach { char, (_, _, features) ->
                for (feature in features.filter { it.executor is FunctionalFeature }) {
                    runCatching {
                        val keyword = (feature.executor!! as FunctionalFeature).keyword(feature.data)
                        if (keyword in this) {
                            error("存在重复的 Functional 关键词: $keyword@${keywords[keyword]}, $char")
                        }
                        this[keyword] = char
                    }.onFailure {
                        Option.TEMPLATE.exception("获取 $char 的 Functional 关键词时遇到错误", it).printStackTrace()
                    }
                }
            }
        }
    }

    operator fun get(keyword: String): Char? = keywords[keyword]

    fun require(keyword: String): Char = get(keyword) ?: Option.TEMPLATE.incorrect("缺少与 Functional 关键词 $keyword 相关联的项目")

    operator fun get(char: Char): String? = keywords.inverse()[char]

    operator fun get(slot: Int): String? = get(holder.shape[slot])


}