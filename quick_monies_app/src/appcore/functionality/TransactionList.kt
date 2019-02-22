package appcore.functionality

class TransactionList {

    val transactions = mutableListOf<Transaction>()

    fun insert(listPosition: Int = -1, transaction: Transaction) {
        if (listPosition in IntRange(0, transactions.size - 1)) {
            transactions.add(listPosition, transaction)
        } else {
            transactions.add(transaction)
        }
    }

    fun remove(listPosition: Int = -1) {
        if (listPosition in IntRange(0, transactions.size - 1)) {
            transactions.removeAt(listPosition)
        } else {
            println("Nothing there, bub. ($listPosition)")
        }
    }

}