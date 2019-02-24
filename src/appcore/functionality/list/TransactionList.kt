package appcore.functionality.list

import appcore.functionality.Transaction

class TransactionList {
    
    val transactions = mutableListOf<Transaction>()
    
    fun insert(listPosition: RelativePos = RelativePos.Last, transaction: Transaction) {
        when (listPosition) {
            RelativePos.First -> transactions.add(0, transaction)
            RelativePos.Last -> transactions.add(transaction)
            
            is RelativePos.Explicit -> {
                if (listPosition.pos in IntRange(1, transactions.size)) {
                    transactions.add(listPosition.pos - 1, transaction)
                }
                else {
                    insert(transaction = transaction)
                }
            }
        }
    }
    
    fun remove(listPosition: RelativePos = RelativePos.Last) {
        when (listPosition) {
            RelativePos.First -> transactions.removeAt(0)
            RelativePos.Last -> transactions.removeAt(transactions.size - 1)
            
            is RelativePos.Explicit -> {
                if (listPosition.pos in IntRange(1, transactions.size)) {
                    transactions.removeAt(listPosition.pos - 1)
                }
                else {
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
    
    fun RelativePos.toAbsoluteIndex() = when (this) {
        RelativePos.Last -> transactions.size - 1
        RelativePos.First -> 0
        is RelativePos.Explicit -> pos - 1
    }
    
}