package kotlin_quick_monies.functionality.accounting

import kotlin_quick_monies.functionality.TimeSinceEpochNumber
import kotlin_quick_monies.functionality.accounting.TransactionTimekeeper.DayOfWeek.*

class TransactionTimekeeper {
    
    private enum class DayOfWeek(isWeekend: Boolean = false) {
        Sunday(true),
        Monday, Tuesday, Wednesday, Thursday, Friday,
        Saturday(true)
    }
    
    private val sundayToSaturdayWeek = listOf(
        Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday
    )
    
    private fun generateNewAccountingWeek() {
        // There is a current set of 'accounts' <-- make the account class
        
    }
    
    private fun testDateStuff() {
        val mapping = linkedMapOf<TimeSinceEpochNumber, TransactionAccountant>()
        
    }
    
}