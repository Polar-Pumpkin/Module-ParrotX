package org.serverct.parrot.parrotx.ui

open class MenuFeatureBase {

    fun require(node: String): Nothing {
        error("缺少 \"$node\" 配置项或给定值无效")
    }

}