package appcore.functionality

class TransactionList {

    val transactions = mutableListOf<Transaction>()

    fun insert(listPosition: RelativePos = RelativePos.Last, transaction: Transaction) {
        when (listPosition) {
            RelativePos.First -> transactions.add(0, transaction)
            RelativePos.Last -> transactions.add(transaction)
            is RelativePos.Explicit -> {
                if (listPosition.pos in IntRange(1, transactions.size)) {
                    transactions.add(listPosition.pos - 1, transaction)
                } else {
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
                } else {
                    remove()
                }
            }
        }
    }

}