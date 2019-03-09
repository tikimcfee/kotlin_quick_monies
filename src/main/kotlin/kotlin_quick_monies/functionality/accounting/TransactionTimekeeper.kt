package kotlin_quick_monies.functionality.accounting

import kotlin_quick_monies.functionality.accounting.TransactionTimekeeper.DayOfWeek.*
import kotlin_quick_monies.functionality.coreDefinitions.Transaction

class TransactionTimekeeper {
    
    private enum class DayOfWeek(isWeekend: Boolean = false) {
        Sunday(true),
        Monday, Tuesday, Wednesday, Thursday, Friday,
        Saturday(true)
    }
    
    private val sundayToSaturdayWeek = listOf(
        Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday
    )
    
    sealed class DayGroup {
        object Week : DayGroup()
        object Month : DayGroup()
        object Year : DayGroup()
    }
    
    data class DateGroupedTransactions<T : DayGroup>(
        val transactions: List<Transaction>,
        val groupedAs: T
    )
}
