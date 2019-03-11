package kotlin_quick_monies.functionality.list

import kotlin_quick_monies.functionality.coreDefinitions.IdealCore.IdealCoreComponent.IdealTransactionSchedule
import kotlin_quick_monies.functionality.coreDefinitions.Transaction

class TransactionList(
    override val id: String = "main_transaction_list"
) : IdealTransactionSchedule() {
    
    override val transactions: MutableList<Transaction> = mutableListOf()
    
    fun insert(listPosition: RelativePos = RelativePos.Last(), transaction: Transaction) {
        when (listPosition) {
            is RelativePos.First -> this.transactions.add(0, transaction)
            is RelativePos.Last -> this.transactions.add(transaction)
            is RelativePos.Explicit -> {
                if (listPosition.index in IntRange(1, this.transactions.size)) {
                    this.transactions.add(listPosition.index - 1, transaction)
                } else {
                    insert(transaction = transaction)
                }
            }
        }
    }
    
    fun remove(listPosition: RelativePos = RelativePos.Last()) {
        when (listPosition) {
            is RelativePos.First -> this.transactions.removeAt(0)
            is RelativePos.Last -> this.transactions.removeAt(this.transactions.size - 1)
            is RelativePos.Explicit -> {
                if (listPosition.index in IntRange(0, this.transactions.size - 1)) {
                    this.transactions.removeAt(listPosition.index)
                } else {
                    remove()
                }
            }
        }
    }
    
    fun move(from: RelativePos, to: RelativePos) {
        this.transactions.removeAt(from.toAbsoluteIndex()).let {
            this.transactions.add(to.toAbsoluteIndex(), it)
        }
    }
    
    private fun RelativePos.toAbsoluteIndex() = when (this) {
        is RelativePos.Last -> this@TransactionList.transactions.size - 1
        is RelativePos.First -> 0
        is RelativePos.Explicit -> index - 1
    }
    
}
