package appcore.functionality.accounting

import appcore.functionality.Transaction
import appcore.functionality.list.TransactionList

class TransactionAccountant {
    
    data class Snapshot(
        val transaction: Transaction,
        val amountBeforeTransaction: Double,
        val amountAfterTransaction: Double
    )
    
    fun computeTransactionDeltas(
        transactionList: TransactionList
    ): List<Snapshot> {
        val transactionCount = transactionList.transactions.size
        
        if (transactionCount == 0) {
            return listOf()
        }
        val deltaList = mutableListOf<Snapshot>()
        
        transactionList.transactions
            .asSequence()
            .sortedBy { it.date.time }
            .mapTo(deltaList) { transaction ->
                if (deltaList.isEmpty()) {
                    transaction.toInitialAccumulator()
                } else {
                    deltaList.last() withAnother transaction
                }
            }
            .reverse()
        
        return deltaList
    }
    
    private fun Transaction.toInitialAccumulator() = Snapshot(
        this, 0.0, this.amount
    )
    
    private infix fun Snapshot.withAnother(transaction: Transaction) = Snapshot(
        transaction, amountAfterTransaction, amountAfterTransaction + transaction.amount
    )
    
}

