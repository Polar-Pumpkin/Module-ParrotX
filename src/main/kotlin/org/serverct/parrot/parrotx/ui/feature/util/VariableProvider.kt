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

interface VariableProvider {

    var name: String

    fun produce(config: MenuConfiguration, data: Map<*, *>, event: ClickEvent, vararg args: Any?): String

    object Registry : UniqueMapRegistry<String, VariableProvider>() {
        override val container: MutableMap<String, VariableProvider> = HashMap()
        override val VariableProvider.key: String
            get() = name
    }

}

fun variableProvider(producer: MenuFeature.Producer<ClickEvent, String>) = object : VariableProvider {
    override var name: String = "Anonymous"
    override fun produce(config: MenuConfiguration, data: Map<*, *>, event: ClickEvent, vararg args: Any?): String {
        return producer.produce(config, data, event, *args)
    }
}

@Awake
internal object VariableProviderRegister : Injector.Fields {
    override val lifeCycle: LifeCycle = LifeCycle.ENABLE
    override val priority: Byte = 0

    override fun inject(field: Field, clazz: Class<*>, instance: Supplier<*>) {
        if (VariableProvider::class.java == field.type || VariableProvider::class.java in field.type.interfaces) {
            val annotation = field.getAnnotation(MenuComponent::class.java) ?: return

            val provider = field.get(instance.get()) as VariableProvider
            if (provider.name == "Anonymous") {
                provider.name = annotation.name.ifBlank { field.name }
            }
            VariableProvider.Registry.register(provider)
        }
    }
}