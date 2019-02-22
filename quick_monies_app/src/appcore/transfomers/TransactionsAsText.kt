package appcore.transfomers

import appcore.functionality.Transaction
import appcore.functionality.TransactionAccountant
import java.text.SimpleDateFormat
import java.util.*

object TransactionsAsText {

    private val yearMonthDay = SimpleDateFormat("yyyy-mm-dd")
    private fun Date.format() = yearMonthDay.format(this)

    class Simple {
        fun Transaction.render() =
                "%s -- %.2f".format(date.format(), amount)

        fun TransactionAccountant.Snapshot.render() =
                "(%.2f)".format(amountAfterTransaction)

    }

}