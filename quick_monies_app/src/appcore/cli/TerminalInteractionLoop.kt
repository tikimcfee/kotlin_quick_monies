package appcore.cli

import appcore.Transaction
import java.util.Date

class TerminalInteractionLoop {

    sealed class Command {
        object Stop : Command()

        class Add(val listPos: Int, val transaction: Transaction) : Command()

        class Remove(val listPos: Int) : Command()
    }

    private var shouldContinue = true

    fun loop(applicationState: ApplicationState) {
//        clear()

        while (shouldContinue) {
            // Grab Input
            print("What's your poison? :: ")
            val rawCommand = readLine() ?: ""
            val parsedCommand = rawCommand.toCommand()

            // Run it
            parsedCommand.execute(applicationState)

            // Redraw the basic stuff, including a clear
            clear()

            outputTransactions(applicationState.transactionList)
            println()

            applicationState.transactionAccountant
                    .computeTransactionDeltas(applicationState.transactionList)
                    .forEach { println(it) }
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

            Command.Stop -> stop()
        }
    }

    private fun stop() {
        shouldContinue = false
    }

    private fun outputTransactions(transactionList: TransactionList) {
        transactionList.transactions.forEachIndexed { index, transaction ->
            println("${index + 1}. $transaction")
        }
    }

    private fun String?.toCommand(): Command {
        val args = this?.split(' ') ?: return Command.Stop

        fun String.isStop() = this == "--" || this == "exit" || this == "x" || this == "quit"

        if (args.size == 1 && args[0].isStop()) {
            return Command.Stop
        }

        val pos = args[1].toInt() - 1

        return when {
            args[0] == "+" -> {
                Command.Add(pos, args.toTransaction())
            }
            args[0] == "-" -> {
                Command.Remove(pos)
            }
            else -> Command.Stop
        }
    }

    private fun List<String>.toTransaction(): Transaction {
        return when (size) {
            2 -> Transaction(Date(), get(1).toDouble())
            3 -> Transaction(Date(), get(2).toDouble())
            else -> Transaction(Date(), 0.0)
        }
    }
}