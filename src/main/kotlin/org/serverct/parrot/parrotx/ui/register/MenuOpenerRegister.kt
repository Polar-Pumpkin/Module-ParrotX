package org.serverct.parrot.parrotx.ui.register

import org.serverct.parrot.parrotx.ui.MenuComponent
import org.serverct.parrot.parrotx.ui.feature.util.MenuOpener
import org.serverct.parrot.parrotx.ui.feature.util.MenuOpenerBuilder
import org.serverct.parrot.parrotx.ui.registry.MenuOpeners
import taboolib.common.LifeCycle
import taboolib.common.inject.Injector
import taboolib.common.platform.Awake
import java.lang.reflect.Field
import java.util.function.Supplier

@Awake
internal object MenuOpenerRegister : Injector.Classes, Injector.Fields {
    override val lifeCycle: LifeCycle = LifeCycle.LOAD
    override val priority: Byte = 0

    override fun inject(clazz: Class<*>, instance: Supplier<*>) {
        if (MenuOpener::class.java.isAssignableFrom(clazz)) {
            MenuOpeners.register(instance.get() as MenuOpener)
        }
    }

    @Suppress("DuplicatedCode")
    override fun inject(field: Field, clazz: Class<*>, instance: Supplier<*>) {
        if (MenuOpener::class.java.isAssignableFrom(field.type)) {
            val annotation = field.getAnnotation(MenuComponent::class.java) ?: return
            val name = annotation.name.ifBlank { field.name }
            val group = clazz.getAnnotation(MenuComponent::class.java)?.let {
                "${it.name.ifBlank { clazz.simpleName }}$"
            } ?: ""

            val opener = field.get(instance.get()) as MenuOpener
            if (opener is MenuOpenerBuilder) {
                opener.name = "$group$name"
            }

            MenuOpeners.register(opener)
        }
    }

    override fun postInject(clazz: Class<*>, instance: Supplier<*>) {
    }
}