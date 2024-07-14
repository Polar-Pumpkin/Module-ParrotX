package org.serverct.parrot.parrotx.builtin

/**
 * reader builtin
 */
interface BuiltinReader<T, V> : Builtin<T, V>, AutoReloadable {

    fun load()

    override fun onLoad() {
        this.load()
    }

    override fun onReload() {
        this.clear()
        this.load()
    }

}