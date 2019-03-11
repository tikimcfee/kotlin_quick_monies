package kotlin_quick_monies.functionality.commands

import com.beust.klaxon.TypeAdapter
import com.beust.klaxon.TypeFor
import kotlin_quick_monies.functionality.commands.Command.*
import kotlin_quick_monies.functionality.coreDefinitions.Transaction
import kotlin.reflect.KClass

const val MAIN_APP_STOP = "--EndOfLine--"
const val ADD_TRANSACTION = "add_transaction"
const val ADD_MONTHLY_TRANSACTION = "add_monthly_transaction"
const val REMOVE_TRANSACTION = "remove_transaction"
const val REMOVE_SCHEDULED_TRANSACTION = "remove_scheduled_transaction"

@TypeFor(field = "commandName", adapter = CommandTypeAdapter::class)
sealed class Command(
    val commandName: String
) {
    
    class MainAppStop : Command(MAIN_APP_STOP)
    
    data class Add(
        val transaction: Transaction
    ) : Command(ADD_TRANSACTION)
    
    data class RemoveTransaction(
        val transactionId: String
    ) : Command(REMOVE_TRANSACTION)
    
    data class AddMonthlyTransaction(
        val monthsToAdd: Int,
        val transactionTemplate: Transaction
    ) : Command(ADD_MONTHLY_TRANSACTION)
    
    data class RemoveScheduledTransaction(
        val groupId: String
    ) : Command(REMOVE_SCHEDULED_TRANSACTION)
}

class CommandTypeAdapter : TypeAdapter<Command> {
    override fun classFor(type: Any): KClass<out Command> = when (type as String) {
        MAIN_APP_STOP -> MainAppStop::class
        ADD_TRANSACTION -> Add::class
        ADD_MONTHLY_TRANSACTION -> AddMonthlyTransaction::class
        REMOVE_TRANSACTION -> RemoveTransaction::class
        REMOVE_SCHEDULED_TRANSACTION -> RemoveScheduledTransaction::class
        else -> throw IllegalArgumentException("Unknown type: $type")
    }
    
}
