package kotlin_quick_monies.functionality.accounting

import kotlin_quick_monies.functionality.coreDefinitions.Transaction
import kotlin_quick_monies.functionality.list.TransactionList

class TransactionAccountant {
    
    data class Snapshot(
        val transaction: Transaction,
        val amountBeforeTransaction: Double,
        val amountAfterTransaction: Double,
        val sourceListPosition: Int
    )
    
    private data class SortedTransaction(
        val transaction: Transaction,
        val sourceListPosition: Int
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
            .sortedBy { it.transaction.date }
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
            sourceListPosition
        )
    
    private infix fun Snapshot.withAnother(transaction: SortedTransaction) =
        Snapshot(
            transaction.transaction,
            amountAfterTransaction,
            amountAfterTransaction + transaction.transaction.amount,
            transaction.sourceListPosition
        )
    
}

