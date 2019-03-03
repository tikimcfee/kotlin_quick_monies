package appcore.functionality

import kotlin_quick_monies.functionality.accounting.TransactionAccountant
import kotlin_quick_monies.functionality.commands.CommandHistorian
import kotlin_quick_monies.functionality.commands.CommandProcessor
import kotlin_quick_monies.functionality.dataPopulation.ProjectedTransactionGenerator
import kotlin_quick_monies.functionality.list.TransactionList


data class ApplicationState(
    val transactionList: TransactionList = TransactionList(),
    val transactionAccountant: TransactionAccountant = TransactionAccountant(),
    val commandProcessor: CommandProcessor = CommandProcessor,
    val commandHistorian: CommandHistorian = CommandHistorian(),
    val projectedTransactionGenerator: ProjectedTransactionGenerator = ProjectedTransactionGenerator()
)