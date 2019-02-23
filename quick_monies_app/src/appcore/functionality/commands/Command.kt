package appcore.functionality.commands

import appcore.functionality.list.RelativePos
import appcore.functionality.Transaction

sealed class Command {
    object MainAppStop : Command()

    class Add(val listPos: RelativePos, val transaction: Transaction) : Command()

    class Remove(val listPos: RelativePos) : Command()
}