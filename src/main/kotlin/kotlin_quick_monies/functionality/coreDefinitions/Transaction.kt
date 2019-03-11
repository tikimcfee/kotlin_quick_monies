package kotlin_quick_monies.functionality.coreDefinitions

import com.beust.klaxon.TypeAdapter
import com.beust.klaxon.TypeFor
import java.util.*
import kotlin.reflect.KClass

data class Transaction(
    override val id: String,
    override val date: Long,
    override val amount: Double,
    override val description: String,
    override val groupInfo: TransactionGroupInfo
) : IdealCore.IdealCoreComponent.IdealTransaction()

data class TransactionGroupInfo(
    override val id: String,
    override val resultTransactions: MutableList<String>,
    override val sourceSchedule: TransactionSchedulingData
) : IdealCore.IdealCoreComponent.IdealTransactionGroupInfo()

open class TransactionSchedulingData(
    override val id: String,
    override val repetitionAmount: Int,
    override val repetitionSeparator: IdealCore.CoreConstants.DayGroup
) : IdealCore.IdealCoreComponent.IdealTransactionSchedulingData()

fun newTransactionId() = UUID.randomUUID().toString()

// ------------------------------------------------------------------------------------------------
// Helpful singletons; group of singles, and schedule of one day for example
// ------------------------------------------------------------------------------------------------

object SingleDaySchedule : TransactionSchedulingData(
    id = "individual_schedule_no_repetition_single_day",
    repetitionAmount = 1,
    repetitionSeparator = IdealCore.CoreConstants.DayGroup.Atom
)

object MonthlySchedule : TransactionSchedulingData(
    id = "individual_schedule_monthly_repetition",
    repetitionAmount = 1,
    repetitionSeparator = IdealCore.CoreConstants.DayGroup.Month
)
