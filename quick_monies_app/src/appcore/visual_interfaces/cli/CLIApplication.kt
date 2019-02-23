package appcore.visual_interfaces.cli

import appcore.functionality.ApplicationState
import appcore.functionality.list.TransactionList

fun main(args: Array<String>) {
    TerminalInteractionLoop().loop(
            ApplicationState(
                    TransactionList()
            )
    )
}

