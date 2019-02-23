package appcore.functionality

import appcore.functionality.accounting.TransactionAccountant
import appcore.functionality.commands.CommandHistorian
import appcore.functionality.commands.CommandProcessor
import appcore.functionality.dataPopulation.ProjectedTransactionGenerator
import appcore.functionality.list.TransactionList


data class ApplicationState(
        val transactionList: TransactionList = TransactionList(),
        val transactionAccountant: TransactionAccountant = TransactionAccountant(),
        val commandProcessor: CommandProcessor = CommandProcessor(),
        val commandHistorian: CommandHistorian = CommandHistorian(),
        val projectedTransactionGenerator: ProjectedTransactionGenerator = ProjectedTransactionGenerator()
)