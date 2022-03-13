package org.serverct.parrot.parrotx.transaction

interface Transaction {

    fun execute(): Boolean

    fun fallback()

}