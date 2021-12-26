package kotlin_quick_monies.functionality.coreDefinitions

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Transaction(
    val id: String,
    var date: Long,
    var amount: Double,
    var description: String,
    val groupInfo: TransactionGroupInfo
)

@Serializable
data class TransactionGroupInfo(
    val id: String,
    val resultTransactions: MutableList<String>,
    val sourceSchedule: TransactionSchedulingData,
    val inHiddenExpenses: Boolean
)

@Serializable
open class TransactionSchedulingData(
    val id: String,
    val repetitionAmount: Int,
    val repetitionSeparator: DayGroup
)

fun newTransactionId() = UUID.randomUUID().toString()

// ------------------------------------------------------------------------------------------------
// Helpful singletons; group of singles, and schedule of one day for example
// ------------------------------------------------------------------------------------------------

object SingleDaySchedule : TransactionSchedulingData(
    id = "individual_schedule_no_repetition_single_day",
    repetitionAmount = 1,
    repetitionSeparator = DayGroup.Day
)
