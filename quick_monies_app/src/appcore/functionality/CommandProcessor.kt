package appcore.functionality

import java.util.*

class CommandProcessor {

    fun parseStringCommand(input: String?) = input.toCommand()

    private fun String?.toCommand(): Command {
        val args = this?.split(' ') ?: return Command.MainAppStop

        fun String.isStop() = this == "--" || this == "exit" || this == "x" || this == "quit"

        if (args.size == 1 && args[0].isStop()) {
            return Command.MainAppStop
        }

        return args.argListToCommand()
    }

    private fun List<String>.argListToCommand() = when {
        get(0) == "+" -> {
            if (size == 2) {
                Command.Add(
                        RelativePos.Last,
                        toTransaction()
                )
            } else {
                Command.Add(
                        RelativePos.Explicit(get(1).toInt()),
                        toTransaction()
                )
            }
        }
        get(0) == "-" -> {
            if (size == 1) {
                Command.Remove(
                        RelativePos.Last
                )
            } else {
                Command.Remove(
                        RelativePos.Explicit(get(1).toInt())
                )
            }

        }
        else -> Command.MainAppStop
    }

    private inline fun List<String>.toTransaction(): Transaction {
        return when (size) {
            2 -> Transaction(Date(), get(1).toDouble())
            3 -> Transaction(Date(), get(2).toDouble())
            else -> Transaction(Date(), 0.0)
        }
    }
}