package org.serverct.parrot.parrotx.function

inline fun <reified T> Map<*, *>.value(node: String): T {
    val value = this[node]
    return value as? T ?: error("缺少配置项 $node 或类型不正确 (要求: ${T::class.simpleName})")
}

inline fun <reified T> Map<*, *>.valueOrNull(node: String): T? {
    val value = this[node] ?: return null
    return value as? T
        ?: error("缺少配置项 $node 或类型不正确: $value (类型: ${T::class.simpleName}, 值: ${value::class.simpleName})")
}

inline fun <reified T> Array<*>.argument(index: Int): T {
    val value = elementAtOrNull(index)
    return value as? T ?: error("缺少参数 $index 或类型不正确 (要求: ${T::class.simpleName})")
}

inline fun <reified T> Array<*>.argumentOrNull(index: Int): T? {
    val value = elementAtOrNull(index) ?: return null
    return value as? T
        ?: error("缺少参数 $index 或类型不正确: $value (类型: ${T::class.simpleName}, 值: ${value::class.simpleName})")
}

fun <T> Iterator<T>.nextOrNull(): T? {
    return if (hasNext()) next() else null
}