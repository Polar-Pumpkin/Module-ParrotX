package org.serverct.parrot.parrotx.ui.register

import org.serverct.parrot.parrotx.ui.MenuFeature
import org.serverct.parrot.parrotx.ui.registry.MenuFeatures
import taboolib.common.LifeCycle
import taboolib.common.inject.Injector
import taboolib.common.platform.Awake
import java.util.function.Supplier

@Awake
internal object MenuFeatureRegister : Injector.Classes {
    override val lifeCycle: LifeCycle = LifeCycle.LOAD
    override val priority: Byte = 0

    override fun inject(clazz: Class<*>, instance: Supplier<*>) {
        if (MenuFeature::class.java.isAssignableFrom(clazz)) {
            MenuFeatures.register(instance.get() as MenuFeature)
        }
    }

    override fun postInject(clazz: Class<*>, instance: Supplier<*>) {
    }
}