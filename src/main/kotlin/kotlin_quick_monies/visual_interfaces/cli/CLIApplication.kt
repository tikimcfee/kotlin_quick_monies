package kotlin_quick_monies.visual_interfaces.cli

import kotlin_quick_monies.functionality.AppStateFunctions
import kotlin_quick_monies.functionality.list.TransactionList

fun main(args: Array<String>) {
    TerminalInteractionLoop().loop(
        AppStateFunctions(
            TransactionList()
        )
    )
}

