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

        val pos = args[1].toInt() - 1

        return when {
            args[0] == "+" -> {
                Command.Add(-1, args.toTransaction())
            }
            args[0] == "-" -> {
                Command.Remove(pos)
            }
            else -> Command.MainAppStop
        }
    }

    private fun List<String>.toTransaction(): Transaction {
        return when (size) {
            2 -> Transaction(Date(), get(1).toDouble())
            3 -> Transaction(Date(), get(2).toDouble())
            else -> Transaction(Date(), 0.0)
        }
    }
}