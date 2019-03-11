package kotlin_quick_monies.functionality.coreDefinitions

sealed class IdealCore {
    
    object CoreConstants {
        enum class DayGroup {
            Atom, Week, Month, Year
        }
    }
    
    sealed class IdealCoreComponent {
        abstract class IdealTransaction : IdealCoreComponent() {
            abstract val id: String
            abstract val description: String
            abstract val amount: Double
            abstract val date: Long
            abstract val groupInfo: IdealTransactionGroupInfo
        }
        
        abstract class IdealTransactionGroupInfo : IdealCoreComponent() {
            abstract val id: String
            abstract val resultTransactions: MutableList<String>
            abstract val sourceSchedule: IdealTransactionSchedulingData
            abstract val inHiddenExpenses: Boolean
        }
        
        abstract class IdealTransactionSchedulingData : IdealCoreComponent() {
            abstract val id: String
            abstract val repetitionAmount: Int
            abstract val repetitionSeparator: CoreConstants.DayGroup
        }
        
        abstract class IdealTransactionSchedule : IdealCoreComponent() {
            abstract val id: String
            abstract val transactions: MutableList<out IdealTransaction>
        }
    }
}
