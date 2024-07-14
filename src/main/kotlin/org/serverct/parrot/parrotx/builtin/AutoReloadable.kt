package org.serverct.parrot.parrotx.builtin

import taboolib.common.LifeCycle
import taboolib.common.inject.ClassVisitor
import taboolib.common.platform.Awake
import taboolib.library.reflex.ClassField
import java.util.function.Supplier

interface AutoReloadable {

    fun onLoad()

    fun onReload()

    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class Load

    @Awake
    class Visitor : ClassVisitor(0) {

        override fun getLifeCycle(): LifeCycle {
            return LifeCycle.ENABLE
        }

        /** 在 LOAD 阶段 唤醒 */
        override fun visitEnd(clazz: Class<*>, instance: Supplier<*>?) {
            if (Builtin::class.java.isAssignableFrom(clazz) && clazz.isAnnotationPresent(Load::class.java)) {
                val registry = instance?.get() ?: return
                if (registry is AutoReloadable) {
                    visit(registry)
                }
            }
        }

        override fun visit(field: ClassField, clazz: Class<*>, instance: Supplier<*>?) {
            if (AutoReloadable::class.java.isAssignableFrom(field.fieldType) && field.name != "INSTANCE") {
                visit(field.get(instance?.get() ?: return) as AutoReloadable)
            }
        }

    }

    companion object {

        private val registry = mutableListOf<AutoReloadable>()

        fun visit(registry: AutoReloadable) {
            registry.onLoad()
            Companion.registry += registry
        }

        fun onReload() {
            registry.forEach { it.onReload() }
        }

    }

}
