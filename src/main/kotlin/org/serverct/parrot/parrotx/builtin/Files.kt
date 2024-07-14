package org.serverct.parrot.parrotx.builtin

import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFile
import taboolib.library.configuration.ConfigurationSection
import java.io.File

/**
 * 创建哈希 builtin
 */
fun <T, V> createHashBuiltin(): BuiltinHash<T, V> {
    return BuiltinHash()
}

/**
 * 创建多文件多节点内建
 * @param path 路径
 * @param resources 资源
 * @param invoker 读取器
 */
fun <T> createDeepMultiBuiltin(path: String, resources: List<String> = emptyList(), invoker: (ConfigurationSection) -> T): BuiltinReader<String, T> {
    return createLocalDeepBuiltin(path, resources, DefaultBuiltinReader.SourceType.DEEP_MULTI, invoker)
}

fun <T> createDeepMultiBuiltin(path: String, vararg resources: String, invoker: (ConfigurationSection) -> T): BuiltinReader<String, T> {
    return createLocalDeepBuiltin(path, listOf(*resources), DefaultBuiltinReader.SourceType.DEEP_MULTI, invoker)
}

/**
 * 创建多文件节点内建
 * @param path 路径
 * @param resources 资源
 * @param invoker 读取器
 */
fun <T> createDeepSingleBuiltin(path: String, resources: List<String> = emptyList(), invoker: (ConfigurationSection) -> T): BuiltinReader<String, T> {
    return createLocalDeepBuiltin(path, resources, DefaultBuiltinReader.SourceType.DEEP_SINGLE, invoker)
}

fun <T> createDeepSingleBuiltin(path: String, vararg resources: String, invoker: (ConfigurationSection) -> T): BuiltinReader<String, T> {
    return createLocalDeepBuiltin(path, listOf(*resources), DefaultBuiltinReader.SourceType.DEEP_SINGLE, invoker)
}

/**
 * 创建多文件节点内建
 * @param path 路径
 * @param resources 资源
 * @param invoker 读取器
 */
fun <T> createLocalDeepBuiltin(path: String, resources: List<String> = emptyList(), type: DefaultBuiltinReader.SourceType, invoker: (ConfigurationSection) -> T): BuiltinReader<String, T> {
    val root = File(getDataFolder(), path)
    // 创建默认资源文件
    if (!root.exists()) {
        resources.forEach { releaseResourceFile("$path/$it", false) }
    }
    if (type == DefaultBuiltinReader.SourceType.SINGLE) {
        error("Cannot create a single builtin with multiple files")
    }
    if (type == DefaultBuiltinReader.SourceType.SINGLE_MULTI) {
        error("Cannot create a single multi builtin with multiple files")
    }
    val builtin = object : DefaultBuiltinReader<T>(root, type) {
        override fun invoke(config: ConfigurationSection): T {
            return invoker(config)
        }
    }
    return builtin
}

/**
 * 创建多文件多节点内建
 * @param path 路径
 * @param invoker 读取器
 */
fun <T> createSingleMultiBuiltin(path: String, invoker: (ConfigurationSection) -> T): BuiltinReader<String, T> {
    return createLocalBuiltin(path, DefaultBuiltinReader.SourceType.SINGLE_MULTI, invoker)
}

/**
 * 创建单文件单节点内建
 * @param path 路径
 * @param invoker 读取器
 */
fun <T> createSingleBuiltin(path: String, invoker: (ConfigurationSection) -> T): BuiltinReader<String, T> {
    return createLocalBuiltin(path, DefaultBuiltinReader.SourceType.SINGLE, invoker)
}

/**
 * 创建单文件节点内建
 * @param path 路径
 * @param type 类型
 * @param invoker 读取器
 */
fun <T> createLocalBuiltin(path: String, type: DefaultBuiltinReader.SourceType, invoker: (ConfigurationSection) -> T): BuiltinReader<String, T> {
    if (type == DefaultBuiltinReader.SourceType.DEEP_MULTI) {
        error("Cannot create a deep multi builtin with a single file")
    }
    if (type == DefaultBuiltinReader.SourceType.DEEP_SINGLE) {
        error("Cannot create a deep single builtin with a single file")
    }

    val file = releaseResourceFile(path, false)
    val builtin = object : DefaultBuiltinReader<T>(file, type) {
        override fun invoke(config: ConfigurationSection): T {
            return invoker(config)
        }
    }
    return builtin
}


fun deepListFiles(file: File): List<File> {
    val result = ArrayList<File>()
    val listFiles = file.listFiles()
    if (listFiles != null) {
        for (child in listFiles) {
            if (child.isDirectory) {
                result.addAll(deepListFiles(child))
            } else {
                result.add(child)
            }
        }
    }
    return result
}