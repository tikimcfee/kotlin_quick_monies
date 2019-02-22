package appcore.functionality


data class ApplicationState(
        val transactionList: TransactionList = TransactionList(),
        val transactionAccountant: TransactionAccountant = TransactionAccountant(),
        val commandProcessor: CommandProcessor = CommandProcessor()
)