package appcore

import appcore.cli.ApplicationState
import appcore.cli.TerminalInteractionLoop
import appcore.cli.TransactionList

fun main(args: Array<String>) {
    TerminalInteractionLoop().loop(
            ApplicationState(
                    TransactionList()
            )
    )
}

