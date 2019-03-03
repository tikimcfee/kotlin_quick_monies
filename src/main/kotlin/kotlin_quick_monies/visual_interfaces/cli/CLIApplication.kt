package kotlin_quick_monies.visual_interfaces.cli

import appcore.functionality.ApplicationState
import kotlin_quick_monies.functionality.list.TransactionList

fun main(args: Array<String>) {
    TerminalInteractionLoop().loop(
        ApplicationState(
            TransactionList()
        )
    )
}

