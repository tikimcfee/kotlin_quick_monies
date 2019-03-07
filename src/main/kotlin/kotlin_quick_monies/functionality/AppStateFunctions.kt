package appcore.functionality

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
    
    fun `apply command to current state`(command: Command) {
        commandHistorian.recordCommand(command)
        command.execute(this)
    }
    
}

fun Command.execute(
    appStateFunctions: AppStateFunctions
): Any = when (this) {
    is Command.Add ->
        appStateFunctions.transactionList.insert(listPos, transaction)
    is Command.Remove ->
        appStateFunctions.transactionList.remove(listPos)
    is Command.Move ->
        appStateFunctions.transactionList.move(from, to)
    is Command.AddMonthlyTransaction -> appStateFunctions.projectedTransactionGenerator
        .`generate and insert a number of monthly transcations from a template`(
            monthsToAdd, transactionTemplate, appStateFunctions
        )
    is Command.MainAppStop -> Unit
}

fun AppStateFunctions.restoreState() {
    println("--- Restoring App State ---")
    commandHistorian.readCommandHistory().forEach { restoredInput ->
        println(".. processing [$restoredInput]")
        with(commandProcessor) {
            with(parseStringCommand(restoredInput)) {
                when (this) {
                    is Command.AddMonthlyTransaction -> {
                        /*
                        todo: Monthly transactions are recorded for historical purposes
                        they add actual transaction commands to the history.. may be best not to do that
                        */
                    }
                    else -> execute(this@restoreState)
                }
            }
        }
    }
}
