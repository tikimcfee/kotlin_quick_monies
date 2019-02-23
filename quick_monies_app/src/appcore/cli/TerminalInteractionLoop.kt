package appcore.cli

import appcore.functionality.ApplicationState
import appcore.functionality.Transaction
import appcore.functionality.accounting.TransactionAccountant
import appcore.functionality.commands.Command
import appcore.functionality.list.RelativePos
import appcore.functionality.list.TransactionList
import appcore.transfomers.TransactionsAsText
import java.util.*

class TerminalInteractionLoop {

    private var shouldContinue = true

    fun loop(applicationState: ApplicationState) {
        clear()
        applicationState.stateLoop()
    }

    private fun ApplicationState.stateLoop() {
        while (shouldContinue) {
            // Grab Input
            print("What's your poison? :: ")
            val input = readLine()
            commandHistorian.recordRawCommand(input ?: "--end-of-input--")

            val commandInput = commandProcessor.parseStringCommand(input)

            // Run it
            with(commandInput) {
                when (this) {
                    is Command.Add ->
                        transactionList.insert(listPos, transaction)
                    is Command.Remove ->
                        transactionList.remove(listPos)
                    Command.Test_AddMultiple ->
                        projectedTransactionGenerator.createMultiple(
                                Transaction(Date(), 125.00),
                                14,
                                25
                        ).forEach {
                            transactionList.insert(RelativePos.Last, it)
                        }
                    Command.MainAppStop ->
                        stop()
                }
            }

            // Redraw the basic stuff, including a clear
            clear()

            outputTransactions(transactionList)
            println()

            outputAccounting(transactionList, transactionAccountant)
            println()
        }
    }

    private fun clear() {
//        println("\u001Bc")
    }


    private fun stop() {
        shouldContinue = false
    }

    private fun outputTransactions(transactionList: TransactionList) {
        transactionList
                .transactions
                .forEachIndexed { index, transaction ->
                    println("${index + 1}. ${TransactionsAsText.Simple.render(transaction)}")
                }
    }

    private fun outputAccounting(transactionList: TransactionList,
                                 transactionAccountant: TransactionAccountant) {
        transactionAccountant
                .computeTransactionDeltas(transactionList)
                .forEachIndexed { index, snapshot ->
                    println("${index + 1}. ${TransactionsAsText.Simple.render(snapshot)}")
                }
    }

}