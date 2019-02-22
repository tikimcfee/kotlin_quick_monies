package appcore.cli

data class ApplicationState(
        val transactionList: TransactionList = TransactionList(),
        val transactionAccountant: TransactionAccountant = TransactionAccountant()
)