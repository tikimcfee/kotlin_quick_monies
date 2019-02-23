package appcore.functionality.accounting

import appcore.functionality.Transaction
import appcore.functionality.list.TransactionList

class TransactionAccountant {

    data class Snapshot(
            val transaction: Transaction,
            val amountBeforeTransaction: Double,
            val amountAfterTransaction: Double
    )

    fun computeTransactionDeltas(transactionList: TransactionList): List<Snapshot> {
        val transactionCount = transactionList.transactions.size

        if (transactionCount == 0) {
            return listOf()
        }

        val deltaList = mutableListOf<Snapshot>()
        var lastAccumulator = transactionList.transactions[0].toInitialAccumulator()
        deltaList.add(lastAccumulator)

        transactionList.transactions
                .asSequence()
                .drop(1)
                .mapTo(deltaList) { transaction ->
                    (lastAccumulator withAnother transaction).also { lastAccumulator = it }
                }

        return deltaList
    }

    private fun Transaction.toInitialAccumulator() = Snapshot(
            this, 0.0, this.amount
    )

    private infix fun Snapshot.withAnother(transaction: Transaction) = Snapshot(
            transaction, amountAfterTransaction, amountAfterTransaction + transaction.amount
    )

}

