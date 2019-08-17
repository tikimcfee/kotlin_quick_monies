package kotlin_quick_monies.functionality.json

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlin_quick_monies.functionality.commands.Command
import kotlin_quick_monies.functionality.commands.Command.*
import kotlin_quick_monies.functionality.commands.PolymorphicJsonAdapterFactory

object JsonTools {
    
    val jsonParser: Moshi = Moshi.Builder()
        .add(
            PolymorphicJsonAdapterFactory.of(Command::class.java, "commandName")
                .withSubtype(Add::class.java, "add_transaction")
                .withSubtype(AddRepeatedTransaction::class.java, "add_repeated_transaction")
                .withSubtype(RemoveTransaction::class.java, "remove_transaction")
                .withSubtype(RemoveScheduledTransaction::class.java, "remove_scheduled_transaction")
                .withSubtype(MainAppStop::class.java, "--EndOfLine--"))
        .add(KotlinJsonAdapterFactory())
        .build()
    
}
