package appcore.transfomers

import appcore.functionality.coreDefinitions.Transaction
import appcore.functionality.accounting.TransactionAccountant
import java.text.SimpleDateFormat
import java.util.*

object TransactionsAsText {
    
    object Simple {
        private val yearMonthDay = SimpleDateFormat("yyyy-MM-dd")
        private fun Date.format() = yearMonthDay.format(this)
        
        fun render(transaction: Transaction) = with(transaction) {
            "%s\t--\t%10.2f".format(date.format(), amount)
        }
        
        
        fun render(snapshot: TransactionAccountant.Snapshot) = with(snapshot) {
            "(%10.2f)".format(amountAfterTransaction)
        }
    }
    
    object QuickMoniesDates {
        private const val yearMonthDayPattern = "yyyy-MM-dd"
        private val yearMonthDayFormat = SimpleDateFormat(yearMonthDayPattern)
        
        fun String.parse(): Date = try {
            yearMonthDayFormat.parse(this)
        } catch (e: Exception) {
            println("Parse failure [$yearMonthDayPattern]<[$this")
            println(e)
            throw e
        }
        
        fun Date.format() = yearMonthDayFormat.format(this)
    }
    
    object IndividualFormatting {
        
        private const val maxPaddedNumber = "%10.2f"
        
        fun Transaction.formattedDate(): String =
            with(QuickMoniesDates) { date.format() }
        
        fun Transaction.formattedAmount() = maxPaddedNumber.format(amount)
        
        fun TransactionAccountant.Snapshot.formattedAfter() =
            maxPaddedNumber.format(amountAfterTransaction)
        
        fun TransactionAccountant.Snapshot.formattedBefore() =
            maxPaddedNumber.format(amountBeforeTransaction)
    }
    
}