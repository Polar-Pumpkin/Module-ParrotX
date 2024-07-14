package org.serverct.parrot.parrotx.builtin

import taboolib.common.platform.function.info
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.util.mapSection
import java.io.File

abstract class DefaultBuiltinReader<T>(val file: File, val sourceType: SourceType) : BuiltinReader<String, T>, BuiltinHash<String, T>() {

    override fun load() {
        this.clear()
        sourceType.load(file).forEach { (key, value) ->
            this[key] = invoke(value)
            info("Loaded $key from ${file.name} with source type $sourceType")
        }
    }

    abstract fun invoke(config: ConfigurationSection): T


    // 读取源类型
    enum class SourceType {

        // 多文件多节点结构读取
        DEEP_MULTI {
            override fun load(file: File): Map<String, ConfigurationSection> {
                val files = deepListFiles(file)
                val map = files.flatMap {
                    SINGLE_MULTI.load(it).entries
                }
                return map.map { it.key to it.value }.toMap()
            }
        },

        // 深层文件夹文件对应节点结构读取
        DEEP_SINGLE {
            override fun load(file: File): Map<String, ConfigurationSection> {
                return deepListFiles(file).map {
                    it.nameWithoutExtension to Configuration.loadFromFile(it)
                }.toMap()
            }


        },

        SINGLE {
            override fun load(file: File): Map<String, ConfigurationSection> {
                val config = Configuration.loadFromFile(file)
                return mapOf(file.nameWithoutExtension to config)
            }
        },

        // 单文件多节点结构读取
        SINGLE_MULTI {
            override fun load(file: File): Map<String, ConfigurationSection> {
                val config = Configuration.loadFromFile(file)
                return config.mapSection { it }
            }
        };

        abstract fun load(file: File): Map<String, ConfigurationSection>

    }
}
