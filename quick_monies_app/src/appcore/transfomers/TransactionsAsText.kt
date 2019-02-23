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

}