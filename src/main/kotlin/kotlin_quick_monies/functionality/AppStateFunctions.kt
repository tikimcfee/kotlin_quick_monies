package kotlin_quick_monies.functionality

import kotlin_quick_monies.functionality.accounting.TransactionAccountant
import kotlin_quick_monies.functionality.commands.Command
import kotlin_quick_monies.functionality.commands.CommandHistorian
import kotlin_quick_monies.functionality.commands.CommandProcessor
import kotlin_quick_monies.functionality.dataPopulation.ProjectedTransactionGenerator
import kotlin_quick_monies.functionality.list.TransactionList


class AppStateFunctions(
    val transactionList: TransactionList = TransactionList(),
    val transactionAccountant: TransactionAccountant = TransactionAccountant(),
    val commandProcessor: CommandProcessor = CommandProcessor,
    val commandHistorian: CommandHistorian = CommandHistorian(),
    val projectedTransactionGenerator: ProjectedTransactionGenerator = ProjectedTransactionGenerator()
) {
    
    fun saveAndRun(command: Command) {
        commandHistorian.recordCommand(command)
        command.executeWithStateFunctions(this)
    }
    
}

fun Command.executeWithStateFunctions(
    appStateFunctions: AppStateFunctions
): Any = when (this) {
    is Command.Add ->
        appStateFunctions.transactionList.insert(transaction = transaction)
    
    is Command.RemoveTransaction ->
        appStateFunctions.transactionList.remove(transactionId)
    
    is Command.AddRepeatedTransaction ->
        appStateFunctions
            .projectedTransactionGenerator
            .insertTransactionsFromTemplate(transactionTemplate, appStateFunctions)
    
    is Command.RemoveScheduledTransaction -> TODO()
    
    is Command.MainAppStop -> Unit
}

fun AppStateFunctions.restoreState() {
    println("--- Restoring App State ---")
    commandHistorian
        .readCommandHistory()
        .forEach { restoredInput ->
            println(".. processing [$restoredInput]")
            commandProcessor
                .parseStringCommand(restoredInput)
                .executeWithStateFunctions(this@restoreState)
        }
}
