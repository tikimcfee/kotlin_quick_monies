package kotlin_quick_monies.visual_interfaces.web

import kotlin_quick_monies.functionality.accounting.TransactionAccountant
import kotlin_quick_monies.functionality.coreDefinitions.Transaction

object LensOfTruth {
    
    fun List<TransactionAccountant.Snapshot>.removeHiddenTransactions():
        Sequence<TransactionAccountant.Snapshot> =
        asSequence().filter { !it.transaction.groupInfo.inHiddenExpenses }
    
    fun List<Transaction>.justTheHiddenOnes():
        Sequence<Transaction> =
        asSequence().filter { it.groupInfo.inHiddenExpenses }
}

