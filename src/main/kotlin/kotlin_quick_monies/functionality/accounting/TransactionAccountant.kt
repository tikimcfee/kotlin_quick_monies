package kotlin_quick_monies.functionality.accounting

import kotlin_quick_monies.functionality.coreDefinitions.Transaction
import kotlin_quick_monies.functionality.list.TransactionList
import org.joda.time.DateTime
import java.util.*

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
        transactionList: TransactionList,
        dateGroupReceiver: ((MutableMap<LongRange, TreeSet<Snapshot>>) -> Unit)? = null
    ): List<Snapshot> {
        val transactionCount = transactionList.transactions.size
        
        if (transactionCount == 0) {
            return listOf()
        }
        
        val deltaList = mutableListOf<Snapshot>()
        val currentGroups: MutableMap<LongRange, TreeSet<Snapshot>> = mutableMapOf()
        
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
                }.also {
                    insertSnapshotIntoSection(currentGroups, TransactionTimekeeper.DayGroup.Month, it)
                }
            }
            .reverse()
        
        dateGroupReceiver?.invoke(currentGroups)
        
        return deltaList
    }
    
    
    private infix fun Long.isBefore(date: Long) = this - date < 0
    
    private infix fun Long.isAfter(date: Long) = this - date > 0
    
    fun dateSortedSetOfSnapshots(reverse: Boolean = true) = TreeSet<Snapshot> { s1, s2 ->
        when {
            s1.transaction.date isBefore s2.transaction.date -> -1
            s1.transaction.date isAfter s2.transaction.date -> 1
            else -> when {
                s1.transaction.amount < s2.transaction.amount -> -1
                s1.transaction.amount > s2.transaction.amount -> 1
                else -> 0
            }
        }.let { if (reverse) it * -1 else it }
    }
    
    private fun insertSnapshotIntoSection(
        currentGroups: MutableMap<LongRange, TreeSet<Snapshot>>,
        dayGroup: TransactionTimekeeper.DayGroup,
        snapshot: Snapshot
    ) {
        val expectedRange = with(snapshot.transaction.date) {
            when (dayGroup) {
                is TransactionTimekeeper.DayGroup.Week -> LongRange(asStartOfWeek().millis, asEndOfWeek().millis)
                is TransactionTimekeeper.DayGroup.Month -> LongRange(asStartOfMonth().millis, asEndOfMonth().millis)
                is TransactionTimekeeper.DayGroup.Year -> LongRange(asStartOfYear().millis, asEndOfYear().millis)
            }
        }
        
        val transactionSet = with(currentGroups[expectedRange]) {
            this ?: dateSortedSetOfSnapshots().also {
                currentGroups[expectedRange] = it
            }
        }
        
        transactionSet.add(snapshot)
    }
    
    private val WEEK_START = 1
    private val WEEK_END = 7
    
    private fun Long.asStartOfWeek() =
        DateTime(this).withTimeAtStartOfDay().withDayOfWeek(WEEK_START)
    
    private fun Long.asEndOfWeek() =
        asStartOfWeek().plusDays(WEEK_END)
    
    private fun Long.asStartOfMonth() =
        DateTime(this).withTimeAtStartOfDay().withDayOfMonth(1)
    
    private fun Long.asEndOfMonth() =
        asStartOfMonth().plusMonths(1).minusDays(1)
    
    private fun Long.asStartOfYear() =
        DateTime(this).withTimeAtStartOfDay().withMonthOfYear(1)
    
    private fun Long.asEndOfYear() =
        DateTime(this).withTimeAtStartOfDay().withMonthOfYear(12)
    
    
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

