package kotlin_quick_monies.transfomers

import kotlin_quick_monies.functionality.accounting.TransactionAccountant
import kotlin_quick_monies.functionality.coreDefinitions.Transaction
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatterBuilder

object TransactionsAsText {
    
    private val yearMonthDayBuilder =
        DateTimeFormatterBuilder()
            .appendYear(4, 4)
            .appendLiteral('-')
            .appendMonthOfYear(2)
            .appendLiteral('-')
            .appendDayOfMonth(2)
    
    private val longMonthLongYearBuilder =
        DateTimeFormatterBuilder()
            .appendMonthOfYearText()
            .appendLiteral(", ")
            .appendYear(4, 4)
    
    private val yearMonthDayFormatter = yearMonthDayBuilder.toFormatter()
    private val longMonthLongYearFormatter = longMonthLongYearBuilder.toFormatter()
    
    object Simple {
        private fun DateTime.format() = yearMonthDayFormatter.print(this)
        
        fun render(transaction: Transaction) = with(transaction) {
            "%s\t--\t%10.2f".format(DateTime(date).format(), amount)
        }
        
        fun render(snapshot: TransactionAccountant.Snapshot) = with(snapshot) {
            "(%10.2f)".format(amountAfterTransaction)
        }
    }
    
    object QuickMoniesDates {
        fun String.parseToDate(): DateTime = try {
            yearMonthDayFormatter.parseDateTime(this)
        } catch (e: Exception) {
            println("Parse failure [$yearMonthDayBuilder]<[$this]")
            println(e)
            throw e
        }
        
        fun DateTime.format(): String = yearMonthDayFormatter.print(this)
    
        fun DateTime.longMonthLongYear(): String = longMonthLongYearFormatter.print(this)
    }
    
    object IndividualFormatting {
        
        private const val maxPaddedNumber = "%10.2f"
        
        fun Transaction.formattedDate(): String =
            with(QuickMoniesDates) { DateTime(date).format() }
        
        fun Transaction.formattedAmount() = maxPaddedNumber.format(amount)
        
        fun TransactionAccountant.Snapshot.formattedAfter() =
            maxPaddedNumber.format(amountAfterTransaction)
        
        fun TransactionAccountant.Snapshot.formattedBefore() =
            maxPaddedNumber.format(amountBeforeTransaction)
    }
    
}
