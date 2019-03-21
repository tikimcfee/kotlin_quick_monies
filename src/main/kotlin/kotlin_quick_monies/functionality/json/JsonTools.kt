package kotlin_quick_monies.functionality.json

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlin_quick_monies.functionality.commands.Command
import kotlin_quick_monies.functionality.commands.PolymorphicJsonAdapterFactory

object JsonTools {
    
    val jsonParser: Moshi = Moshi.Builder()
        .apply {
            PolymorphicJsonAdapterFactory.of(Command::class.java, "commandName")
                
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
    
}
