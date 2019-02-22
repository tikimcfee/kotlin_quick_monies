package appcore.cli

import appcore.functionality.ApplicationState
import appcore.functionality.Command
import appcore.functionality.TransactionAccountant
import appcore.functionality.TransactionList
import appcore.transfomers.TransactionsAsText

class TerminalInteractionLoop {

    private var shouldContinue = true

    fun loop(applicationState: ApplicationState) {
        clear()

        while (shouldContinue) {
            // Grab Input
            print("What's your poison? :: ")
            val input = readLine()
            val commandInput = applicationState.commandProcessor.parseStringCommand(input)

            // Run it
            commandInput.execute(applicationState)

            // Redraw the basic stuff, including a clear
            clear()

            outputTransactions(applicationState.transactionList)
            println()

            outputAccounting(
                    applicationState.transactionList,
                    applicationState.transactionAccountant
            )
            println()
        }
    }

    private fun clear() = println("\u001Bc")

    private fun Command.execute(applicationState: ApplicationState) {
        when (this) {
            is Command.Add -> {
                applicationState.transactionList.insert(listPos, transaction)
            }

            is Command.Remove -> {
                applicationState.transactionList.remove(listPos)
            }

            Command.MainAppStop -> stop()
        }
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