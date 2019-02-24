package appcore.functionality.commands

import appcore.functionality.Transaction
import appcore.functionality.list.RelativePos

sealed class Command {
    
    object MainAppStop : Command()
    
    object Test_AddMultiple : Command()
    
    class Add(
        val listPos: RelativePos,
        val transaction: Transaction
    ) : Command()
    
    class Remove(val listPos: RelativePos) : Command()
    
    class Move(
        val from: RelativePos,
        val to: RelativePos
    ) : Command()
    
}