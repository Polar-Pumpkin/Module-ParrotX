package org.serverct.parrot.parrotx.ui

@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class MenuComponent(val name: String = "")
