package kotlin_quick_monies.functionality.commands

import kotlin_quick_monies.functionality.json.JsonTools.jsonParser
import com.squareup.moshi.JsonAdapter

object CommandProcessor {
    
    private val commandAdapter: JsonAdapter<Command> = jsonParser.adapter(Command::class.java)
    
    fun parseStringCommand(input: String?) =
        input?.let {
            commandAdapter.fromJson(it)
        } ?: Command.MainAppStop()
    
    fun Command.toJsonString(): String =
        commandAdapter.toJson(this)
}
