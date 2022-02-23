package org.serverct.parrot.parrotx.ui

import taboolib.common.LifeCycle
import taboolib.common.inject.Injector
import taboolib.common.platform.Awake
import java.util.function.Supplier

@Awake
internal object MenuFeatureRegister : Injector.Classes {
    override val lifeCycle: LifeCycle = LifeCycle.ENABLE
    override val priority: Byte = 0

    override fun inject(clazz: Class<*>, instance: Supplier<*>) {
        if (MenuFeature::class.java != clazz.superclass) {
            return
        }
        MenuFeature.Registry.register(instance.get() as MenuFeature)
    }

    override fun postInject(clazz: Class<*>, instance: Supplier<*>) {
    }
}