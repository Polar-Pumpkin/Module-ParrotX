@file:Isolated

package org.serverct.parrot.parrotx.mechanism

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import taboolib.common.Isolated
import taboolib.common.LifeCycle
import taboolib.common.inject.ClassVisitor
import taboolib.common.platform.Awake
import taboolib.library.reflex.ClassMethod
import java.util.function.Supplier

@Isolated
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Reloadable

@Awake
@Isolated
@Suppress("unused")
object Reloadables : ClassVisitor() {

    private val registered: Multimap<Any, ClassMethod> = HashMultimap.create()

    override fun getLifeCycle(): LifeCycle = LifeCycle.LOAD

    override fun visit(method: ClassMethod, clazz: Class<*>, instance: Supplier<*>?) {
        if (method.isAnnotationPresent(Reloadable::class.java)) {
            registered.put(instance?.get() ?: return, method)
        }
    }

    fun execute() {
        registered.asMap().forEach { (instance, methods) ->
            methods.forEach {
                it.invoke(instance)
            }
        }
    }
}