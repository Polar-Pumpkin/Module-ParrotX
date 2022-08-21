package org.serverct.parrot.parrotx.ui.register

import org.serverct.parrot.parrotx.ui.MenuComponent
import org.serverct.parrot.parrotx.ui.feature.util.VariableProvider
import org.serverct.parrot.parrotx.ui.feature.util.VariableProviderBuilder
import org.serverct.parrot.parrotx.ui.registry.VariableProviders
import taboolib.common.LifeCycle
import taboolib.common.inject.Injector
import taboolib.common.platform.Awake
import java.lang.reflect.Field
import java.util.function.Supplier

@Awake
internal object VariableProviderRegister : Injector.Classes, Injector.Fields {
    override val lifeCycle: LifeCycle = LifeCycle.LOAD
    override val priority: Byte = 0

    override fun inject(clazz: Class<*>, instance: Supplier<*>) {
        if (VariableProvider::class.java.isAssignableFrom(clazz)) {
            VariableProviders.register(instance.get() as VariableProvider)
        }
    }

    @Suppress("DuplicatedCode")
    override fun inject(field: Field, clazz: Class<*>, instance: Supplier<*>) {
        if (VariableProvider::class.java.isAssignableFrom(field.type)) {
            val annotation = field.getAnnotation(MenuComponent::class.java) ?: return
            val name = annotation.name.ifBlank { field.name }
            val group = clazz.getAnnotation(MenuComponent::class.java)?.let {
                "${it.name.ifBlank { clazz.simpleName }}$"
            } ?: ""

            val provider = field.get(instance.get()) as VariableProvider
            if (provider is VariableProviderBuilder) {
                provider.name = "$group$name"
            }

            VariableProviders.register(provider)
        }
    }

    override fun postInject(clazz: Class<*>, instance: Supplier<*>) {
    }
}