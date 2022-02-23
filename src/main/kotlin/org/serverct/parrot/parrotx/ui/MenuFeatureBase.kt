package org.serverct.parrot.parrotx.ui

open class MenuFeatureBase {

    val String.indicator: String
        get() = substringBefore('$')

    val String.keyword: String
        get() = substringAfter('$')

    val String?.menuKeyword: Triple<String, String, String>
        get() = if (this != null) Triple(this, indicator, keyword) else Triple("", "", "")

    fun require(node: String): Nothing {
        error("缺少 \"$node\" 配置项或给定值无效")
    }

}