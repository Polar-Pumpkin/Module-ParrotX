package org.serverct.parrot.parrotx.ui.register

import org.serverct.parrot.parrotx.ui.MenuComponent
import org.serverct.parrot.parrotx.ui.feature.util.MenuFunction
import org.serverct.parrot.parrotx.ui.feature.util.MenuFunctionBuilder
import org.serverct.parrot.parrotx.ui.registry.MenuFunctions
import taboolib.common.LifeCycle
import taboolib.common.inject.Injector
import taboolib.common.platform.Awake
import java.lang.reflect.Field
import java.util.function.Supplier

@Awake
internal object MenuFunctionRegister : Injector.Classes, Injector.Fields {
    override val lifeCycle: LifeCycle = LifeCycle.LOAD
    override val priority: Byte = 0

    override fun inject(clazz: Class<*>, instance: Supplier<*>) {
        if (MenuFunction::class.java.isAssignableFrom(clazz)) {
            MenuFunctions.register(instance.get() as MenuFunction)
        }
    }


    @Suppress("DuplicatedCode")
    override fun inject(field: Field, clazz: Class<*>, instance: Supplier<*>) {
        if (MenuFunction::class.java.isAssignableFrom(field.type)) {
            val annotation = field.getAnnotation(MenuComponent::class.java) ?: return
            val name = annotation.name.ifBlank { field.name }
            val group = clazz.getAnnotation(MenuComponent::class.java)?.let {
                "${it.name.ifBlank { clazz.simpleName }}$"
            } ?: ""

            val function = field.get(instance.get()) as MenuFunction
            if (function is MenuFunctionBuilder) {
                function.name = "$group$name"
            }

            MenuFunctions.register(function)
        }
    }

    override fun postInject(clazz: Class<*>, instance: Supplier<*>) {
    }
}