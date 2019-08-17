package kotlin_quick_monies.functionality

import kotlin_quick_monies.functionality.accounting.SortedList
import kotlin_quick_monies.functionality.accounting.TransactionAccountant

typealias CSVEntry = Map.Entry<LongRange, SortedList<TransactionAccountant.Snapshot>>

val CSVEntry.dateRange: LongRange
    get() = key
val CSVEntry.transactions: List<TransactionAccountant.Snapshot>
    get() = value

fun CSVEntry.onTransactions(
    receiver: (TransactionAccountant.Snapshot) -> Unit
) = transactions.forEach(receiver)

fun TransactionAccountant.Snapshot.eachValue(receiver: (Any) -> Unit) {
    receiver(this.transaction.date)
    receiver(this.transaction.description)
    receiver(this.amountBeforeTransaction)
    receiver(this.transaction.amount)
    receiver(this.amountAfterTransaction)
    receiver(this.transaction.id)
}

fun <T> StringBuilder.wrapValueIfString(toAppend: T): StringBuilder =
    when (toAppend) {
        is CharSequence -> append('"').append(toAppend).append('"')
        is String -> append('"').append(toAppend).append('"')
        else -> append(toAppend)
    }
    

fun StringBuilder.csvBreak(): StringBuilder = append("\n")

fun CSVEntry.toRawCsv(): String =
    StringBuilder()
        .apply {
            onTransactions { snapshot ->
                snapshot.eachValue {
                    wrapValueIfString(it)
                    append('\t')
                }
                deleteCharAt(length-1)
                csvBreak()
            }
        }
        .toString()

object CSVWrapper {
    fun AppStateFunctions.exportCurrentSnapshotsToCsv() {
        println("--- Starting .csv export ---")
        transactionAccountant.computeTransactionDeltas(
            transactionList,
            dateGroupReceiver = { dateMap: TransactionAccountant.DateRangeSnapshotMultiMap ->
                dateMap.allEntries().forEach {
                    print(it.toRawCsv())
                }
            })
        println("")
        println("--- Ending .csv export ---")
    }
}
