package kotlin_quick_monies.functionality.commands

import com.beust.klaxon.Klaxon

object CommandProcessor {
    
    private val jsonParser = Klaxon()
    
    fun parseStringCommand(input: String?) = input.toCommand()
    
    fun Command.toJsonString() = jsonParser.toJsonString(this)
    
    private fun String?.toCommand(): Command {
        return this?.let {
            jsonParser.parse<Command>(it)
        } ?: Command.MainAppStop()
    }
    
}