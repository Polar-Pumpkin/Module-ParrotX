package org.serverct.parrot.parrotx.ui

data class MenuKeyword(val content: String, val indicator: String, val keyword: String) {

    constructor(content: String, delimiter: Char = '$') : this(
        content,
        content.substringBefore(delimiter),
        content.substringAfter(delimiter, "")
    )

    companion object {
        private val EMPTY_KEYWORD: MenuKeyword by lazy { MenuKeyword("", "", "") }
        private val cached: MutableMap<String, MenuKeyword> = HashMap()

        fun of(content: String?, delimiter: Char = '$'): MenuKeyword {
            return content?.let { cached.computeIfAbsent(it) { _ -> MenuKeyword(it, delimiter) } } ?: EMPTY_KEYWORD
        }
    }
}
