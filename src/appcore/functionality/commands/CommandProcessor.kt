package appcore.functionality.commands

import appcore.functionality.Transaction
import appcore.functionality.list.RelativePos
import java.util.*


class CommandProcessor {
    
    fun parseStringCommand(input: String?) = input.toCommand()
    
    fun String.isStop() = this == "--" || this == "exit" || this == "x" || this == "quit"
    
    private fun String?.toCommand(): Command {
        val args = this?.split(' ') ?: return Command.MainAppStop
        
        if (args.size == 1 && args[0].isStop()) {
            return Command.MainAppStop
        }
        
        return args.argListToCommand()
    }
    
    private fun List<String>.argListToCommand() = when (get(0)) {
        "+" -> {
            if (size == 2) {
                Command.Add(
                    RelativePos.Last,
                    toTransaction()
                )
            }
            else {
                Command.Add(
                    RelativePos.Explicit(get(1).toInt()),
                    toTransaction()
                )
            }
        }
        
        "-" -> {
            if (size == 1) {
                Command.Remove(
                    RelativePos.Last
                )
            }
            else {
                Command.Remove(
                    RelativePos.Explicit(get(1).toInt())
                )
            }
            
        }
        
        "m" -> {
            Command.Move(
                RelativePos.Explicit(get(1).toInt()),
                RelativePos.Explicit(get(2).toInt())
            )
        }
        
        "*" -> {
            Command.Test_AddMultiple
        }
        
        else -> Command.MainAppStop
    }
    
    private fun List<String>.toTransaction(): Transaction {
        return when (size) {
            2 -> Transaction(Date(), get(1).toDouble(), subList(2, size).joinToString())
            3 -> Transaction(Date(), get(2).toDouble(), subList(3, size).joinToString())
            else -> Transaction(Date(), 0.0, "[${joinToString()}]")
        }
    }
}