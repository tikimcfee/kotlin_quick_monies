package appcore.functionality.accounting

import appcore.functionality.Transaction
import appcore.functionality.list.TransactionList

class TransactionAccountant {
    
    data class Snapshot(
        val transaction: Transaction,
        val amountBeforeTransaction: Double,
        val amountAfterTransaction: Double,
        val originalPos: Int
    )
    
    private data class SortedTransaction(
        val transaction: Transaction,
        val originalPos: Int
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
            .withIndex()
            .map { SortedTransaction(it.value, it.index) }
            .sortedBy { it.transaction.date.time }
            .mapTo(deltaList) { sortedTransaction ->
                if (deltaList.isEmpty()) {
                    sortedTransaction.toInitialAccumulator()
                } else {
                    deltaList.last() withAnother sortedTransaction
                }
            }
            .reverse()
        
        return deltaList
    }
    
    private fun SortedTransaction.toInitialAccumulator() =
        Snapshot(
            transaction,
            0.0,
            this.transaction.amount,
            originalPos
        )
    
    private infix fun Snapshot.withAnother(transaction: SortedTransaction) =
        Snapshot(
            transaction.transaction,
            amountAfterTransaction,
            amountAfterTransaction + transaction.transaction.amount,
            transaction.originalPos
        )
    
}

