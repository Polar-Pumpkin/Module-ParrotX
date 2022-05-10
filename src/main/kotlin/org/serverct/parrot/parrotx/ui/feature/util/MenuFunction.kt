package org.serverct.parrot.parrotx.ui.feature.util

import org.bukkit.inventory.ItemStack
import org.serverct.parrot.parrotx.container.SimpleRegistry
import org.serverct.parrot.parrotx.ui.MenuComponent
import org.serverct.parrot.parrotx.ui.config.MenuConfiguration
import org.serverct.parrot.parrotx.ui.MenuFeature
import taboolib.common.LifeCycle
import taboolib.common.inject.Injector
import taboolib.common.platform.Awake
import taboolib.module.ui.ClickEvent
import java.lang.reflect.Field
import java.util.function.Supplier

interface MenuFunction {

    var name: String

    fun buildIcon(config: MenuConfiguration, data: Map<*, *>, icon: ItemStack, vararg args: Any?): ItemStack

    fun handle(config: MenuConfiguration, data: Map<*, *>, event: ClickEvent, vararg args: Any?)

    object Registry : SimpleRegistry<String, MenuFunction>() {
        override val registered: MutableMap<String, MenuFunction> = HashMap()
        override val MenuFunction.key: String
            get() = name
    }

    class Builder {
        internal val onBuildIcons = HashSet<MenuFeature.Producer<ItemStack, ItemStack>>()
        internal val onClicks = HashSet<MenuFeature.Handler<ClickEvent>>()

        fun onBuildIcon(handler: MenuFeature.Producer<ItemStack, ItemStack>) = onBuildIcons.add(handler)

        fun onClick(handler: MenuFeature.Handler<ClickEvent>) = onClicks.add(handler)
    }

}

fun menuFunction(builder: MenuFunction.Builder.() -> Unit) = object : MenuFunction {
    override var name: String = "Anonymous"
    private val builder = MenuFunction.Builder().apply(builder)

    override fun buildIcon(config: MenuConfiguration, data: Map<*, *>, icon: ItemStack, vararg args: Any?): ItemStack {
        var result = icon.clone()
        this.builder.onBuildIcons.forEach { result = it.produce(config, data, result, *args) }
        return result
    }

    override fun handle(config: MenuConfiguration, data: Map<*, *>, event: ClickEvent, vararg args: Any?) {
        this.builder.onClicks.forEach { it.handle(config, data, event, *args) }
    }
}

@Awake
internal object MenuFunctionRegister : Injector.Classes, Injector.Fields {
    override val lifeCycle: LifeCycle = LifeCycle.ENABLE
    override val priority: Byte = 0

    override fun inject(clazz: Class<*>, instance: Supplier<*>) {
        if (MenuFunction::class.java in clazz.interfaces) {
            MenuFunction.Registry.register(instance.get() as MenuFunction)
        }
    }

    private val functions: MutableMap<String, MutableMap<String, MenuFunction>> = HashMap()

    override fun inject(field: Field, clazz: Class<*>, instance: Supplier<*>) {
        if (MenuFunction::class.java == field.type || MenuFunction::class.java in field.type.interfaces) {
            val annotation = field.getAnnotation(MenuComponent::class.java) ?: return
            val name = annotation.name.ifBlank { field.name }
            val function = field.get(instance.get()) as MenuFunction

            functions.compute(clazz.name) { _, members ->
                (members ?: HashMap()).also { it[name] = function }
            }
        }
    }

    override fun postInject(clazz: Class<*>, instance: Supplier<*>) {
        val members = functions.remove(clazz.name) ?: return
        val group = clazz.getAnnotation(MenuComponent::class.java)?.let {
            "${it.name.ifBlank { clazz.simpleName }}$"
        } ?: ""

        members.forEach { (name, function) ->
            if (function.name == "Anonymous") {
                function.name = "$group$name"
            }
            MenuFunction.Registry.register(function)
        }
    }
}