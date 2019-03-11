package kotlin_quick_monies.functionality.list

import kotlin_quick_monies.functionality.coreDefinitions.IdealCore.IdealCoreComponent.IdealTransactionSchedule
import kotlin_quick_monies.functionality.coreDefinitions.Transaction

class TransactionList(
    override val id: String = "main_transaction_list"
) : IdealTransactionSchedule() {
    
    override val transactions: MutableList<Transaction> = mutableListOf()
    
    fun insert(transaction: Transaction): Boolean {
        return transactions.add(transaction)
    }
    
    fun remove(transactionId: String): Boolean {
        return transactions.removeAll { it.id == transactionId }
    }
    
}
