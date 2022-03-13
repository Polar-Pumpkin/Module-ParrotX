package org.serverct.parrot.parrotx.transaction

import java.util.*

class TransactionBuilder {

    private val transactions: Queue<Transaction> = LinkedList()

    fun then(transaction: Transaction): TransactionBuilder {
        transactions.offer(transaction)
        return this
    }

}