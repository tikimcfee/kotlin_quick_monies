package kotlin_quick_monies.functionality.commands

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory


object CommandProcessor {
    
    private val jsonParser = Moshi.Builder()
        .apply {
            PolymorphicJsonAdapterFactory
                .of(Command::class.java, "commandName")
                .withSubtype(Command.Add::class.java, "add_transaction")
                .withSubtype(Command.AddRepeatedTransaction::class.java, "add_repeated_transaction")
                .withSubtype(Command.RemoveTransaction::class.java, "remove_transaction")
                .withSubtype(Command.RemoveScheduledTransaction::class.java, "remove_scheduled_transaction")
                .withSubtype(Command.MainAppStop::class.java, "--EndOfLine--")
                .let {
                    add(it)
                }
        }
        .add(KotlinJsonAdapterFactory())
        .build()
    private val commandAdapter: JsonAdapter<Command> = jsonParser.adapter(Command::class.java)
    
    fun parseStringCommand(input: String?) = input.toCommand()
    
    fun Command.toJsonString() = commandAdapter.toJson(this)
    
    private fun String?.toCommand(): Command {
        return this?.let {
            commandAdapter.fromJson(it)
        } ?: Command.MainAppStop()
    }
    
}
