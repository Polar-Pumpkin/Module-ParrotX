package org.serverct.parrot.parrotx.ui

import org.bukkit.inventory.ItemStack
import org.serverct.parrot.parrotx.container.SimpleRegistry
import taboolib.module.ui.ClickEvent

abstract class MenuFeature : MenuFeatureBase() {

    abstract val name: String

    open fun buildIcon(config: MenuConfiguration, data: Map<*, *>, icon: ItemStack, vararg args: Any?): ItemStack = icon

    open fun handle(config: MenuConfiguration, data: Map<*, *>, event: ClickEvent, vararg args: Any?) {}

    companion object {
        fun map(config: MenuConfiguration, data: Map<*, *>): MappedMenuFeature = MappedMenuFeature.map(config, data)

        fun mapAll(config: MenuConfiguration, datas: List<Map<*, *>>): List<MappedMenuFeature> =
            MappedMenuFeature.mapAll(config, datas)
    }

    object Registry : SimpleRegistry<String, MenuFeature>() {
        override val registered: MutableMap<String, MenuFeature> = HashMap()
        override val MenuFeature.key: String
            get() = name
    }

    fun interface Producer<E, V> {
        fun produce(config: MenuConfiguration, data: Map<*, *>, element: E, vararg args: Any?): V
    }

    fun interface Handler<E> {
        fun handle(config: MenuConfiguration, data: Map<*, *>, element: E, vararg args: Any?)
    }

}