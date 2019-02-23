package appcore.transfomers

import appcore.functionality.Transaction
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

    object IndividualFormatting {
        private val yearMonthDay = SimpleDateFormat("yyyy-MM-dd")
        private const val sharedNumberFormat = "%10.2f"

        private fun Date.format() = yearMonthDay.format(this)

        fun Transaction.formattedDate() = date.format()

        fun Transaction.formattedAmount() = sharedNumberFormat.format(amount)

        fun TransactionAccountant.Snapshot.formattedAfter() = sharedNumberFormat.format(amountAfterTransaction)

        fun TransactionAccountant.Snapshot.formattedBefore() = sharedNumberFormat.format(amountBeforeTransaction)
    }

}