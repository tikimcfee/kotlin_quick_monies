package kotlin_quick_monies.functionality.commands

import kotlin_quick_monies.functionality.coreDefinitions.Transaction
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val MAIN_APP_STOP = "--EndOfLine--"
const val ADD_TRANSACTION = "add_transaction"
const val ADD_REPEATED_TRANSACTION = "add_repeated_transaction"
const val REMOVE_TRANSACTION = "remove_transaction"
const val REMOVE_SCHEDULED_TRANSACTION = "remove_scheduled_transaction"
const val UPDATE_TRANSACTION_AMOUNT = "update_transaction_amount"

@Serializable
sealed class Command() {
    
    @Serializable
    @SerialName(MAIN_APP_STOP)
    object MainAppStop : Command()
    
    @Serializable
    @SerialName(ADD_TRANSACTION)
    data class Add(
        val transaction: Transaction
    ) : Command()
    
    @Serializable
    @SerialName(REMOVE_TRANSACTION)
    data class RemoveTransaction(
        val transactionId: String
    ) : Command()
    
    @Serializable
    @SerialName(ADD_REPEATED_TRANSACTION)
    data class AddRepeatedTransaction(
        val transactionTemplate: Transaction
    ) : Command()
    
    @Serializable
    @SerialName(REMOVE_SCHEDULED_TRANSACTION)
    data class RemoveScheduledTransaction(
        val groupId: String
    ) : Command()

    @Serializable
    @SerialName(UPDATE_TRANSACTION_AMOUNT)
    data class UpdateTransactionAmount(
        val transactionId: String,
        val newAmount: Double
    ) : Command()
}

