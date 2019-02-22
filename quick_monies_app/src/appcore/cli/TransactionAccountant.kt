package appcore.cli

import appcore.Transaction

class TransactionAccountant {

    fun computeTransactionDeltas(transactionList: TransactionList): List<TransactionAccumulation> {
        if (transactionList.transactions.size == 0) {
            return listOf()
        }

        val deltaList = mutableListOf<TransactionAccumulation>()
        var lastAccumulator = transactionList.transactions[0].toInitialAccumulator()
        deltaList.add(lastAccumulator)

        transactionList.transactions
                .asSequence()
                
                .mapTo(deltaList) { transaction ->
                    (lastAccumulator withAnother transaction).also { lastAccumulator = it }
                }

        return deltaList
    }

    private fun Transaction.toInitialAccumulator() = TransactionAccumulation(
            this, 0.0, this.amount
    )

    private infix fun TransactionAccumulation.withAnother(transaction: Transaction) = TransactionAccumulation(
            transaction, amountAfterTransaction, amountAfterTransaction + transaction.amount
    )

    data class TransactionAccumulation(
            val transaction: Transaction,
            val amountBeforeTransaction: Double,
            val amountAfterTransaction: Double
    )
}

