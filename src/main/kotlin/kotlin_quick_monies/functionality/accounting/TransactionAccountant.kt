package kotlin_quick_monies.functionality.accounting

import kotlin_quick_monies.functionality.coreDefinitions.IdealCore
import kotlin_quick_monies.functionality.coreDefinitions.IdealCore.CoreConstants.DayGroup.*
import kotlin_quick_monies.functionality.coreDefinitions.Transaction
import kotlin_quick_monies.functionality.list.TransactionList
import java.util.*


class TransactionAccountant {
    
    data class Snapshot(
        val transaction: Transaction,
        val amountBeforeTransaction: Double,
        val amountAfterTransaction: Double
    )
    
    class DateRangeSnapshotMultiMap : SortedMultiMap<LongRange, Snapshot>() {
        override fun compare(o1: Snapshot?, o2: Snapshot?): Int {
            if (o1 == null) {
                return if (o2 == null) {
                    0
                } else {
                    1
                }
            } else if (o2 == null) {
                return -1
            }
            
            return SortOption.Date.applyTo(o1.transaction, o2.transaction)
        }
        
        fun addTransaction(rangeKey: LongRange, snapshot: Snapshot) {
            val targetList = this[rangeKey] ?: SortedList(this).also {
                this[rangeKey] = it
            }
            
            targetList.add(snapshot)
        }
    }
    
    fun computeTransactionDeltas(
        transactionList: TransactionList,
        dateGroupReceiver: ((DateRangeSnapshotMultiMap) -> Unit)? = null,
        sortOptions: SortOption = SortOption.Date
    ): List<Snapshot> {
        val transactionCount = transactionList.transactions.size
        
        if (transactionCount == 0) {
            return listOf()
        }
        
        val deltaList = mutableListOf<Snapshot>()
        val currentGroups = DateRangeSnapshotMultiMap()
        
        transactionList.transactions
            .asSequence()
//            .filter {
//                // Here's where we the play the game of hide and seek.
//                // We know it's hidden. But guess what...
//                // We're gonna compute with it anyway.
//                !it.groupInfo.inHiddenExpenses
//            }
            .sortedWith(Comparator { t1, t2 ->
                sortOptions.applyTo(t1, t2)
            })
            .mapTo(deltaList) { sortedTransaction ->
                if (deltaList.isEmpty()) {
                    sortedTransaction.toInitialAccumulator()
                } else {
                    deltaList.last() withAnother sortedTransaction
                }.also {
                    insertSnapshotIntoSection(
                        currentGroups = currentGroups,
                        dayGroup = IdealCore.CoreConstants.DayGroup.Month,
                        snapshot = it
                    )
                }
            }
        
        dateGroupReceiver?.invoke(currentGroups)
        
        return deltaList
    }
    
    private fun insertSnapshotIntoSection(
        currentGroups: DateRangeSnapshotMultiMap,
        dayGroup: IdealCore.CoreConstants.DayGroup,
        snapshot: Snapshot
    ) {
        val expectedRange = with(snapshot.transaction.date) {
            when (dayGroup) {
                Atom -> LongRange(this, this)
                Week -> LongRange(asStartOfWeek().millis, asEndOfWeek().millis)
                Month -> LongRange(asStartOfMonth().millis, asEndOfMonth().millis)
                Year -> LongRange(asStartOfYear().millis, asEndOfYear().millis)
            }
        }
        
        currentGroups.addTransaction(expectedRange, snapshot)
    }
    
    private fun Transaction.toInitialAccumulator() =
        Snapshot(
            this,
            0.0,
            amount
        )
    
    private infix fun Snapshot.withAnother(transaction: Transaction) =
        Snapshot(
            transaction,
            amountAfterTransaction,
            amountAfterTransaction + transaction.amount
        )
    
}

