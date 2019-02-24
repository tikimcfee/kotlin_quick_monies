package appcore.functionality.commands

import appcore.functionality.Transaction
import appcore.functionality.list.RelativePos
import jdk.internal.util.xml.impl.ReaderUTF8
import java.io.IOException
import java.io.StreamTokenizer
import java.util.*

object WholeLineFeeder {
    
    //fun String?.toCommand(): Command {
    //    this ?: return Command.MainAppStop
    //
    //    val aliasRegexSplat =
    //        "(${Command.allAliases().joinToString("|")})"
    //
    //    val commandFromAlias = Regex(aliasRegexSplat).find(this)?.let {
    //        Command.fromStringAlias(it.value)
    //    } ?: Command.MainAppStop
    //
    //    when (commandFromAlias) {
    //        Command.MainAppStop -> TODO()
    //        Command.Test_AddMultiple -> TODO()
    //        is Command.Add -> TODO()
    //        is Command.Remove -> TODO()
    //        is Command.Move -> TODO()
    //    }
    //
    //    return Command.Add(
    //        RelativePos.Last,
    //        Transaction(
    //            Date(),
    //            782.22,
    //            "Testing input feed: [$this]"
    //        )
    //    )
    //}
    
    
    
    fun String.test() {
        val tokenizer = StreamTokenizer(ReaderUTF8(byteInputStream()))
        
        
        
        try {
            do {
                val token = tokenizer.nextToken()
                when (tokenizer.ttype) {
                    '\''.toInt() -> {
                        TODO("the value is literally a quote char")
                    }
                    
                    '"'.toInt() -> {
                        val stringBody = tokenizer.sval
                    }
                    
                    StreamTokenizer.TT_WORD -> {
                        val wordCharacters = tokenizer.sval
                    }
                    
                    StreamTokenizer.TT_NUMBER -> {
                    
                    }
                    
                    StreamTokenizer.TT_EOF -> {
                    
                    }
                    
                    StreamTokenizer.TT_EOL -> {
                    
                    }
                }
            } while (tokenizer.ttype != StreamTokenizer.TT_EOF)
            
        }
        catch (readFailure: IOException) {
            println(readFailure)
        }
    }
    
}

class CommandProcessor {
    
    fun __test__wholeLineFeed(input: String?) = with(WholeLineFeeder) {
        input.toCommand()
    }
    
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