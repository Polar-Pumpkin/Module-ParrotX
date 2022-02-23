package org.serverct.parrot.parrotx.ui.feature.util

import org.serverct.parrot.parrotx.container.UniqueMapRegistry
import org.serverct.parrot.parrotx.ui.MenuComponent
import org.serverct.parrot.parrotx.ui.MenuConfiguration
import org.serverct.parrot.parrotx.ui.MenuFeature
import taboolib.common.LifeCycle
import taboolib.common.inject.Injector
import taboolib.common.platform.Awake
import taboolib.module.ui.ClickEvent
import java.lang.reflect.Field
import java.util.function.Supplier

interface MenuOpener {

    var name: String

    fun open(config: MenuConfiguration, data: Map<*, *>, event: ClickEvent, vararg args: Any?)

    object Registry : UniqueMapRegistry<String, MenuOpener>() {
        override val container: MutableMap<String, MenuOpener> = HashMap()
        override val MenuOpener.key: String
            get() = name
    }

}

fun menuOpener(handler: MenuFeature.Handler<ClickEvent>) = object : MenuOpener {
    override var name: String = "Anonymous"
    override fun open(config: MenuConfiguration, data: Map<*, *>, event: ClickEvent, vararg args: Any?) {
        handler.handle(config, data, event, *args)
    }
}

@Awake
internal object MenuOpenerRegister : Injector.Classes, Injector.Fields {
    override val lifeCycle: LifeCycle = LifeCycle.ENABLE
    override val priority: Byte = 0

    override fun inject(clazz: Class<*>, instance: Supplier<*>) {
        if (MenuOpener::class.java in clazz.interfaces) {
            MenuOpener.Registry.register(instance.get() as MenuOpener)
        }
    }

    private val openers: MutableMap<String, MutableMap<String, MenuOpener>> = HashMap()

    override fun inject(field: Field, clazz: Class<*>, instance: Supplier<*>) {
        if (MenuOpener::class.java == field.type || MenuOpener::class.java in field.type.interfaces) {
            val annotation = field.getAnnotation(MenuComponent::class.java) ?: return
            val name = annotation.name.ifBlank { field.name }
            val opener = field.get(instance.get()) as MenuOpener

            openers.compute(clazz.name) { _, members ->
                (members ?: HashMap()).also { it[name] = opener }
            }
        }
    }

    override fun postInject(clazz: Class<*>, instance: Supplier<*>) {
        val members = openers.remove(clazz.name) ?: return
        val group = clazz.getAnnotation(MenuComponent::class.java)?.let {
            "${it.name.ifBlank { clazz.simpleName }}$"
        } ?: ""

        members.forEach { (name, opener) ->
            if (opener.name == "Anonymous") {
                opener.name = "$group$name"
            }
            MenuOpener.Registry.register(opener)
        }
    }

}
