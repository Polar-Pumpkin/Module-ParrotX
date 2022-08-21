package org.serverct.parrot.parrotx.util

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import taboolib.common.LifeCycle
import taboolib.common.inject.Injector
import taboolib.common.platform.Awake
import java.lang.reflect.Method
import java.util.function.Supplier

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
internal annotation class Reloadable

@Awake
@Suppress("unused")
internal object Reloadables : Injector.Methods {
    override val lifeCycle: LifeCycle = LifeCycle.LOAD
    override val priority: Byte = 0

    private val registered: Multimap<Any, Method> = HashMultimap.create()

    override fun inject(method: Method, clazz: Class<*>, instance: Supplier<*>) {
        if (method.isAnnotationPresent(Reloadable::class.java)) {
            registered.put(instance.get(), method)
        }
    }

    fun execute() {
        registered.asMap().forEach { (instance, methods) ->
            methods.forEach {
                it(instance)
            }
        }
    }
}