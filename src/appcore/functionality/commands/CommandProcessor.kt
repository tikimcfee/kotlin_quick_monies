package appcore.functionality.commands

import appcore.functionality.coreDefinitions.Transaction
import appcore.functionality.list.RelativePos
import java.util.*

class CommandProcessor {
    
    fun parseStringCommand(input: String?) = input.toCommand()
    
    private fun String?.toCommand(): Command {
        val args = this?.split(' ') ?: return Command.MainAppStop
        return args.argListToCommand()
    }
    
    private fun List<String>.actionIsAddToAccount() = get(0) == "add"
    private fun List<String>.actionIsremoveFromAccount() = get(0) == "remove"
    private fun List<String>.actionIsMovePositionInAccount() = get(0) == "move"
    private fun List<String>.actionIsAddMultipleToAccount() = get(0) == "*"
    
    private fun List<String>.actionIsStop() = get(0) == "stop"
    
    private fun List<String>.argListToCommand() = when (get(0)) {
        "add" -> {
            val relativePos = positionFromArgIndex(1)
            val amount = get(2).toDouble()
            val date = Date(get(3).toLong())
            val description = if (size >= 5) {
                subList(4, size).joinToString(" ")
            } else {
                "{no description}"
            }
            
            Command.Add(
                relativePos,
                Transaction(date, amount, description)
            )
        }
        
        "remove" -> {
            val relativePos = positionFromArgIndex(1)
            Command.Remove(relativePos)
        }
        
        "move" -> {
            Command.Move(
                positionFromArgIndex(1),
                positionFromArgIndex(2)
            )
        }
        
        "*" -> {
            Command.Test_AddMultiple
        }
        
        "stop" -> {
            Command.MainAppStop
        }
        
        else -> Command.MainAppStop
    }
    
    private fun List<String>.positionFromArgIndex(index: Int = 1): RelativePos {
        val positionArg = get(index).toInt()
        return when (positionArg) {
            -1 -> RelativePos.First
            -2 -> RelativePos.Last
            else -> RelativePos.Explicit(positionArg)
        }
    }
}