package kotlin_quick_monies.functionality.accounting

import kotlin_quick_monies.functionality.accounting.TransactionTimekeeper.DayGroup.*
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
    
    enum class SortOptions {
        FutureStuffAtTheBottom,
        FutureStuffAtTheTop,
        ExpensiveStuffAtTop,
        ExpensiveStuffAtBottom,
    }
    
    private fun List<SortOptions>.applyTo(st1: Transaction, st2: Transaction): Int {
        var totalDelta = 0
        forEach {
            val dateDelta = when {
                st1.date isBefore st2.date -> -1
                st1.date isAfter st2.date -> 1
                else -> 0
            }
            val amountDelta = when {
                st1.amount < st2.amount -> -1
                st1.amount > st2.amount -> 1
                else -> 0
            }
            totalDelta += when (it) {
                SortOptions.FutureStuffAtTheBottom -> dateDelta
                SortOptions.FutureStuffAtTheTop -> -dateDelta
                SortOptions.ExpensiveStuffAtTop -> -amountDelta
                SortOptions.ExpensiveStuffAtBottom -> amountDelta
            }
        }
        return totalDelta
    }
    
    fun computeTransactionDeltas(
        transactionList: TransactionList,
        dateGroupReceiver: ((MutableMap<LongRange, TreeSet<Snapshot>>) -> Unit)? = null,
        sortOptions: List<SortOptions> = listOf(SortOptions.FutureStuffAtTheBottom)
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
            .sortedWith(Comparator { t1, t2 ->
                sortOptions.applyTo(t1.transaction, t2.transaction)
            })
            .mapTo(deltaList) { sortedTransaction ->
                if (deltaList.isEmpty()) {
                    sortedTransaction.toInitialAccumulator()
                } else {
                    deltaList.last() withAnother sortedTransaction
                }.also {
                    insertSnapshotIntoSection(
                        currentGroups = currentGroups,
                        sortOptions = sortOptions,
                        dayGroup = Month,
                        snapshot = it
                    )
                }
            }
        
        dateGroupReceiver?.invoke(currentGroups)
        
        return deltaList
    }
    
    private infix fun Long.isBefore(date: Long) = this - date < 0
    
    private infix fun Long.isAfter(date: Long) = this - date > 0
    
    private fun insertSnapshotIntoSection(
        currentGroups: MutableMap<LongRange, TreeSet<Snapshot>>,
        sortOptions: List<SortOptions>,
        dayGroup: TransactionTimekeeper.DayGroup,
        snapshot: Snapshot
    ) {
        val expectedRange = with(snapshot.transaction.date) {
            when (dayGroup) {
                is Week -> LongRange(asStartOfWeek().millis, asEndOfWeek().millis)
                is Month -> LongRange(asStartOfMonth().millis, asEndOfMonth().millis)
                is Year -> LongRange(asStartOfYear().millis, asEndOfYear().millis)
            }
        }
        
        val transactionSet = with(currentGroups[expectedRange]) {
            this ?: dateSortedSetOfSnapshots(sortOptions).also {
                currentGroups[expectedRange] = it
            }
        }
        
        transactionSet.add(snapshot)
    }
    
    private fun dateSortedSetOfSnapshots(
        sortOptions: List<SortOptions>
    ) = TreeSet<Snapshot> { s1, s2 ->
        sortOptions.applyTo(
            s1.transaction, s2.transaction
        )
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

