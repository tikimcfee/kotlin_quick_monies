package appcore.functionality

import appcore.functionality.commands.Command
import appcore.functionality.commands.Command.*
import appcore.functionality.coreDefinitions.Transaction
import appcore.functionality.dataPopulation.ProjectedTransactionGenerator
import appcore.functionality.list.RelativePos
import appcore.functionality.list.TransactionList
import java.util.*

fun Command.execute(
    transactionList: TransactionList,
    projectedTransactionGenerator: ProjectedTransactionGenerator
): Boolean {
    when (this) {
        is Add -> transactionList.insert(listPos, transaction)
        is Remove -> transactionList.remove(listPos)
        is Move -> transactionList.move(from, to)
        Test_AddMultiple ->
            projectedTransactionGenerator.createMultiple(
                Transaction(
                    date = Date(),
                    amount = 125.00,
                    description = "Testing Multiple Additions"
                ),
                14,
                25
            ).forEach {
                transactionList.insert(RelativePos.Last, it)
            }
        MainAppStop -> return false
    }
    return true
}

fun ApplicationState.restoreState() {
    println("--- Restoring App State ---")
    commandHistorian.readCommandHistory().forEach { restoredInput ->
        println(".. processing [$restoredInput]")
        with(commandProcessor) {
            if (restoredInput == MainAppStop.singleLineSerialization())
                return@with
            
            parseStringCommand(restoredInput).execute(
                transactionList,
                projectedTransactionGenerator
            )
        }
    }
}