package kotlin_quick_monies.functionality.commands

import com.squareup.moshi.JsonAdapter
import kotlin_quick_monies.functionality.json.JsonTools.jsonParser


object CommandProcessor {
    
    private val commandAdapter: JsonAdapter<Command> = jsonParser.adapter(Command::class.java)
    
    fun parseStringCommand(input: String?) =
        input?.let {
            commandAdapter.fromJson(input)
        } ?: Command.MainAppStop()
    
    fun Command.toJsonString(): String =
        commandAdapter.toJson(this)
}
