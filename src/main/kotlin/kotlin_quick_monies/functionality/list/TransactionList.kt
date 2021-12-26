package kotlin_quick_monies.functionality.list

import kotlin_quick_monies.functionality.coreDefinitions.Transaction

class TransactionList(
    val id: String = "main_transaction_list"
) {
    
    val transactions: MutableList<Transaction> = mutableListOf()
    
    fun insert(transaction: Transaction): Boolean {
        return transactions.add(transaction)
    }
    
    fun remove(transactionId: String): Boolean {
        return transactions.removeAll { it.id == transactionId }
    }

    fun update(transactionId: String, receiver: (Transaction) -> Unit) {
        transactions.forEach {
            if (it.id.contains(transactionId)) {
                receiver(it)
            }
        }
    }

}
