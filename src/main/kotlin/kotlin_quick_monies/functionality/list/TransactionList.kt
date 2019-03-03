package kotlin_quick_monies.functionality.list

import kotlin_quick_monies.functionality.coreDefinitions.Transaction

class TransactionList {
    
    val transactions = mutableListOf<Transaction>()
    
    fun insert(listPosition: RelativePos = RelativePos.Last(), transaction: Transaction) {
        when (listPosition) {
            is RelativePos.First -> transactions.add(0, transaction)
            is RelativePos.Last -> transactions.add(transaction)
            is RelativePos.Explicit -> {
                if (listPosition.index in IntRange(1, transactions.size)) {
                    transactions.add(listPosition.index - 1, transaction)
                } else {
                    insert(transaction = transaction)
                }
            }
        }
    }
    
    fun remove(listPosition: RelativePos = RelativePos.Last()) {
        when (listPosition) {
            is RelativePos.First -> transactions.removeAt(0)
            is RelativePos.Last -> transactions.removeAt(transactions.size - 1)
            is RelativePos.Explicit -> {
                if (listPosition.index in IntRange(0, transactions.size - 1)) {
                    transactions.removeAt(listPosition.index)
                } else {
                    remove()
                }
            }
        }
    }
    
    fun move(from: RelativePos, to: RelativePos) {
        transactions.removeAt(from.toAbsoluteIndex()).let {
            transactions.add(to.toAbsoluteIndex(), it)
        }
    }
    
    private fun RelativePos.toAbsoluteIndex() = when (this) {
        is RelativePos.Last -> transactions.size - 1
        is RelativePos.First -> 0
        is RelativePos.Explicit -> index - 1
    }
    
}