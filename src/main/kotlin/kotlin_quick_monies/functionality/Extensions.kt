package appcore.functionality

import kotlin_quick_monies.functionality.commands.Command
import kotlin_quick_monies.functionality.commands.Command.*
import kotlin_quick_monies.functionality.coreDefinitions.Transaction
import kotlin_quick_monies.functionality.dataPopulation.ProjectedTransactionGenerator
import kotlin_quick_monies.functionality.list.RelativePos
import kotlin_quick_monies.functionality.list.TransactionList
import java.util.*

fun Command.execute(
    transactionList: TransactionList,
    projectedTransactionGenerator: ProjectedTransactionGenerator
): Boolean {
    when (this) {
        is Add -> transactionList.insert(listPos, transaction)
        is Remove -> transactionList.remove(listPos)
        is Move -> transactionList.move(from, to)
        is Test_AddMultiple ->
            projectedTransactionGenerator.createMultiple(
                Transaction(
                    date = Date().time,
                    amount = 125.00,
                    description = "Testing Multiple Additions"
                ),
                14,
                25
            ).forEach {
                transactionList.insert(RelativePos.Last(), it)
            }
        is MainAppStop -> return false
    }
    return true
}

fun ApplicationState.restoreState() {
    println("--- Restoring App State ---")
    commandHistorian.readCommandHistory().forEach { restoredInput ->
        println(".. processing [$restoredInput]")
        with(commandProcessor) {
            if (restoredInput == MainAppStop().toJsonString())
                return@with
            
            parseStringCommand(restoredInput).execute(
                transactionList,
                projectedTransactionGenerator
            )
        }
    }
}