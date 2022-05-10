package org.serverct.parrot.parrotx.ui.config

@Suppress("MemberVisibilityCanBePrivate")
internal enum class Option(val path: String, val display: String) {
    TITLE("Title", "标题"),
    ROWS("Rows", "行数"),
    SHAPE("Shape", "样式映射"),
    TEMPLATE("Template", "物品模板");

    val formatted: String
        get() = "$display($path)"

    fun missing(): Nothing = error("Gui 配置缺失或无效: $formatted")

    fun incorrect(reason: String): Nothing = error("Gui 配置不正确@$formatted: $reason")

    fun exception(message: String, cause: Throwable? = null): IllegalStateException {
        val context = "Gui 配置不正确@$formatted: $message"
        return if (cause == null) IllegalStateException(context) else IllegalStateException(context, cause)
    }
}