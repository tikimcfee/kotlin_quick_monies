package appcore.cli

import appcore.functionality.ApplicationState
import appcore.functionality.TransactionList

fun main(args: Array<String>) {
    TerminalInteractionLoop().loop(
            ApplicationState(
                    TransactionList()
            )
    )
}

