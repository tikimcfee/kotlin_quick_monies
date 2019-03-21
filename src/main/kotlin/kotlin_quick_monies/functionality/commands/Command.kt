package kotlin_quick_monies.functionality.commands

import kotlin_quick_monies.functionality.coreDefinitions.Transaction

const val MAIN_APP_STOP = "--EndOfLine--"
const val ADD_TRANSACTION = "add_transaction"
const val ADD_REPEATED_TRANSACTION = "add_repeated_transaction"
const val REMOVE_TRANSACTION = "remove_transaction"
const val REMOVE_SCHEDULED_TRANSACTION = "remove_scheduled_transaction"

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
    
    data class AddRepeatedTransaction(
        val transactionTemplate: Transaction
    ) : Command(ADD_REPEATED_TRANSACTION)
    
    data class RemoveScheduledTransaction(
        val groupId: String
    ) : Command(REMOVE_SCHEDULED_TRANSACTION)
}
