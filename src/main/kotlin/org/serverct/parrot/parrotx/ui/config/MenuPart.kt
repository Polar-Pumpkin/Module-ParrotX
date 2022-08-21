package org.serverct.parrot.parrotx.ui.config

internal enum class MenuPart(val display: String, vararg val paths: String) {
    DEBUG("调试模式", "debug"),
    TITLE("标题", "title"),
    SHAPE("布局", "shape", "layout"),
    TEMPLATE("模板", "template", "item");

    val formatted: String by lazy { "$display(${paths.joinToString("/")})" }

    fun missing(): Nothing = throw IllegalArgumentException("GUI 配置缺失或无效: $formatted")

    infix fun incorrect(reason: String): Nothing = throw IllegalArgumentException("GUI 配置 $formatted 不正确: $reason")

    infix fun incorrect(context: Pair<String, Throwable>) {
        val (reason, cause) = context
        IllegalArgumentException("GUI 配置 $formatted 不正确: $reason", cause).printStackTrace()
    }
}